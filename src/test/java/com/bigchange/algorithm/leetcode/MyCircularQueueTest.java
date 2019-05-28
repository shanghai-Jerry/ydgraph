package com.bigchange.algorithm.leetcode; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* MyCircularQueue Tester. 
* 
* @author <Authors name> 
* @since <pre>May 28, 2019</pre> 
* @version 1.0 
*/ 
public class MyCircularQueueTest {

  MyCircularQueue myCircularQueue;

@Before
public void before() throws Exception {
  myCircularQueue = new MyCircularQueue(10);
} 

@After
public void after() throws Exception {
} 

/** 
* 
* Method: enQueue(int value) 
* 
*/ 
@Test
public void testEnQueue() throws Exception { 
//TODO: Test goes here...
  myCircularQueue.enQueue(0);
} 

/** 
* 
* Method: deQueue() 
* 
*/ 
@Test
public void testDeQueue() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: Front() 
* 
*/ 
@Test
public void testFront() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: Rear() 
* 
*/ 
@Test
public void testRear() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: isEmpty() 
* 
*/ 
@Test
public void testIsEmpty() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: isFull() 
* 
*/ 
@Test
public void testIsFull() throws Exception { 
//TODO: Test goes here... 
} 


} 
