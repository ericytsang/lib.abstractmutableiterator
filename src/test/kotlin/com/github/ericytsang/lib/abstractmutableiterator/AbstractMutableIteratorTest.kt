package com.github.ericytsang.lib.abstractmutableiterator

import org.junit.Test
import java.util.NoSuchElementException

/**
 * Created by surpl on 8/20/2016.
 */
class AbstractMutableIteratorTest
{
    val removedItems = mutableListOf<Int>()

    val testIterator = object:AbstractMutableIterator<Int,Int>()
    {
        private val backingIterator = listOf(1,2,3,4).iterator()

        override fun computeNext()
        {
            try
            {
                val value = backingIterator.next()
                setNext(value,value)
            }
            catch (ex:NoSuchElementException)
            {
                done()
            }
        }

        override fun doRemove(metaData:Int)
        {
            removedItems.add(metaData)
        }
    }

    @Test
    fun nextUntilEnd()
    {
        assert(testIterator.next() == 1)
        assert(testIterator.next() == 2)
        assert(testIterator.next() == 3)
        assert(testIterator.next() == 4)
        assert(removedItems.isEmpty())
    }

    @Test
    fun nextBeyondEnd()
    {
        assert(testIterator.next() == 1)
        assert(testIterator.next() == 2)
        assert(testIterator.next() == 3)
        assert(testIterator.next() == 4)
        try
        {
            testIterator.next()
            assert(false)
        }
        catch (ex:NoSuchElementException)
        {
            // should catch exception
        }
        assert(removedItems.isEmpty())
    }

    @Test
    fun nextWithHasNextUntilEnd()
    {
        assert(testIterator.hasNext())
        assert(testIterator.next() == 1)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 2)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 3)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 4)
        assert(!testIterator.hasNext())
        assert(removedItems.isEmpty())
    }

    @Test
    fun nextWithHasNextBeyondEnd()
    {
        assert(testIterator.hasNext())
        assert(testIterator.next() == 1)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 2)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 3)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 4)
        assert(!testIterator.hasNext())
        try
        {
            testIterator.next()
            assert(false)
        }
        catch (ex:NoSuchElementException)
        {
            // should catch exception
        }
        assert(removedItems.isEmpty())
    }

    @Test
    fun nextWithRemoveUntilEnd()
    {
        assert(testIterator.next() == 1)
        testIterator.remove()
        assert(testIterator.next() == 2)
        assert(testIterator.next() == 3)
        testIterator.remove()
        assert(testIterator.next() == 4)
        testIterator.remove()
        assert(removedItems == listOf(1,3,4))
    }

    @Test
    fun nextWithRemoveBeyondEnd()
    {
        assert(testIterator.next() == 1)
        assert(testIterator.next() == 2)
        testIterator.remove()
        assert(testIterator.next() == 3)
        testIterator.remove()
        assert(testIterator.next() == 4)
        try
        {
            testIterator.next()
            assert(false)
        }
        catch (ex:NoSuchElementException)
        {
            // should catch exception
        }
        assert(removedItems == listOf(2,3))
    }

    @Test
    fun nextWithHasNextWithRemoveUntilEnd()
    {
        assert(testIterator.hasNext())
        assert(testIterator.next() == 1)
        testIterator.remove()
        assert(testIterator.hasNext())
        assert(testIterator.next() == 2)
        assert(testIterator.hasNext())
        testIterator.remove()
        assert(testIterator.next() == 3)
        testIterator.remove()
        assert(testIterator.hasNext())
        assert(testIterator.next() == 4)
        assert(!testIterator.hasNext())
        assert(removedItems == listOf(1,2,3))
    }

    @Test
    fun consecutiveRemove()
    {
        assert(testIterator.next() == 1)
        assert(testIterator.next() == 2)
        testIterator.remove()
        try
        {
            testIterator.remove()
            assert(false)
        }
        catch (ex:IllegalStateException)
        {
            // should catch exception
        }
        assert(removedItems == listOf(2))
    }
}
