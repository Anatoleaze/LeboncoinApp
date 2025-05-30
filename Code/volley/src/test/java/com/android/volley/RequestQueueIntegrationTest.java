/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley;

import com.android.volley.Request.Priority;
import com.android.volley.RequestQueue.RequestFinishedListener;
import com.android.volley.mock.MockRequest;
import com.android.volley.mock.ShadowSystemClock;
import com.android.volley.toolbox.NoCache;
import com.android.volley.utils.ImmediateResponseDelivery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Integration tests for {@link RequestQueue} that verify its behavior in conjunction with real
 * dispatcher, queues and Requests.
 *
 * <p>The Network is mocked out.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowSystemClock.class})
public class RequestQueueIntegrationTest {

    private ResponseDelivery mDelivery;
    @Mock private Network mMockNetwork;
    @Mock private RequestFinishedListener<byte[]> mMockListener;
    @Mock private RequestFinishedListener<byte[]> mMockListener2;

    @Before public void setUp() throws Exception {
        mDelivery = new ImmediateResponseDelivery();
        initMocks(this);
    }

    @Test public void add_requestProcessedInCorrectOrder() throws Exception {
        // Enqueue 2 requests with different cache keys, and different priorities. The second,
        // higher priority request takes 20ms.
        // Assert that the first request is only handled after the first one has been parsed and
        // delivered.
        MockRequest lowerPriorityReq = new MockRequest();
        MockRequest higherPriorityReq = new MockRequest();
        lowerPriorityReq.setCacheKey("1");
        higherPriorityReq.setCacheKey("2");
        lowerPriorityReq.setPriority(Priority.LOW);
        higherPriorityReq.setPriority(Priority.HIGH);

        Answer<NetworkResponse> delayAnswer = new Answer<NetworkResponse>() {
            @Override
            public NetworkResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(20);
                return mock(NetworkResponse.class);
            }
        };
        // delay only for higher request
        when(mMockNetwork.performRequest(higherPriorityReq)).thenAnswer(delayAnswer);
        when(mMockNetwork.performRequest(lowerPriorityReq)).thenReturn(mock(NetworkResponse.class));

        RequestQueue queue = new RequestQueue(new NoCache(), mMockNetwork, 1, mDelivery);
        queue.addRequestFinishedListener(mMockListener);
        queue.add(lowerPriorityReq);
        queue.add(higherPriorityReq);
        queue.start();

        InOrder inOrder = inOrder(mMockListener);
        // verify higherPriorityReq goes through first
        inOrder.verify(mMockListener, timeout(10000)).onRequestFinished(higherPriorityReq);
        // verify lowerPriorityReq goes last
        inOrder.verify(mMockListener, timeout(10000)).onRequestFinished(lowerPriorityReq);

        queue.stop();
    }

    /** Asserts that requests with same cache key are processed in order. */
    @Test public void add_dedupeByCacheKey() throws Exception {
        // Enqueue 2 requests with the same cache key. The first request takes 20ms. Assert that the
        // second request is only handled after the first one has been parsed and delivered.
        MockRequest req1 = new MockRequest();
        MockRequest req2 = new MockRequest();
        Answer<NetworkResponse> delayAnswer = new Answer<NetworkResponse>() {
            @Override
            public NetworkResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(20);
                return mock(NetworkResponse.class);
            }
        };
        //delay only for first
        when(mMockNetwork.performRequest(req1)).thenAnswer(delayAnswer);
        when(mMockNetwork.performRequest(req2)).thenReturn(mock(NetworkResponse.class));

        RequestQueue queue = new RequestQueue(new NoCache(), mMockNetwork, 3, mDelivery);
        queue.addRequestFinishedListener(mMockListener);
        queue.add(req1);
        queue.add(req2);
        queue.start();

        InOrder inOrder = inOrder(mMockListener);
        // verify req1 goes through first
        inOrder.verify(mMockListener, timeout(10000)).onRequestFinished(req1);
        // verify req2 goes last
        inOrder.verify(mMockListener, timeout(10000)).onRequestFinished(req2);

        queue.stop();
    }

    /** Verify RequestFinishedListeners are informed when requests are canceled.  */
    @Test public void add_requestFinishedListenerCanceled() throws Exception {
        MockRequest request = new MockRequest();
        Answer<NetworkResponse> delayAnswer = new Answer<NetworkResponse>() {
            @Override
            public NetworkResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(200);
                return mock(NetworkResponse.class);
            }
        };
        RequestQueue queue = new RequestQueue(new NoCache(), mMockNetwork, 1, mDelivery);

        when(mMockNetwork.performRequest(request)).thenAnswer(delayAnswer);

        queue.addRequestFinishedListener(mMockListener);
        queue.start();
        queue.add(request);

        request.cancel();
        verify(mMockListener, timeout(10000)).onRequestFinished(request);
        queue.stop();
    }

    /** Verify RequestFinishedListeners are informed when requests are successfully delivered. */
    @Test public void add_requestFinishedListenerSuccess() throws Exception {
        MockRequest request = new MockRequest();
        RequestQueue queue = new RequestQueue(new NoCache(), mMockNetwork, 1, mDelivery);

        queue.addRequestFinishedListener(mMockListener);
        queue.addRequestFinishedListener(mMockListener2);
        queue.start();
        queue.add(request);

        verify(mMockListener, timeout(10000)).onRequestFinished(request);
        verify(mMockListener2, timeout(10000)).onRequestFinished(request);

        queue.stop();
    }

    /** Verify RequestFinishedListeners are informed when request errors. */
    @Test public void add_requestFinishedListenerError() throws Exception {
        MockRequest request = new MockRequest();
        RequestQueue queue = new RequestQueue(new NoCache(), mMockNetwork, 1, mDelivery);

        when(mMockNetwork.performRequest(request)).thenThrow(new VolleyError());

        queue.addRequestFinishedListener(mMockListener);
        queue.start();
        queue.add(request);

        verify(mMockListener, timeout(10000)).onRequestFinished(request);
        queue.stop();
    }
}
