package com.github.ericytsang.lib.abstractmutableiterator

import java.util.NoSuchElementException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Created by surpl on 8/20/2016.
 */
abstract class AbstractMutableIterator<Element,ElementMetaData>():MutableIterator<Element>
{
    private val mutexLock = ReentrantLock()

    protected abstract fun computeNext():Unit

    protected abstract fun doRemove(metaData:ElementMetaData)

    protected fun done() = nextDataGenerator.done()

    protected fun setNext(value:Element,metaData:ElementMetaData) = nextDataGenerator.setNext(value,metaData)

    final override fun hasNext():Boolean = mutexLock.withLock()
    {
        return try
        {
            nextDataGenerator.tryGetNextData()
            true
        }
        catch (ex:NoSuchElementException)
        {
            false
        }
    }

    final override fun next():Element = mutexLock.withLock()
    {
        return nextDataGenerator.tryGetNextDataAndConsume().element
    }

    final override fun remove() = mutexLock.withLock()
    {
        doRemove(nextDataGenerator.tryGetMetaDataOfLastConsumedNextData())
    }

    private val nextDataGenerator = object
    {
        private var nextDataGetter:(()->NextData<Element,ElementMetaData>)? = null

        private var metaDataGetter:()->ElementMetaData = {throw IllegalStateException("the next method has not yet been called")}

        fun tryGetNextData():NextData<Element,ElementMetaData>
        {
            // compute the next element if it has yet to be computed
            if (nextDataGetter == null) computeNext()

            // if element not computed at this point, we know computeNext is implemented incorrectly
            if (nextDataGetter == null) throw IllegalStateException("computeNext implemented incorrectly. it must call done() or setNext() before returning.")

            // return...
            return nextDataGetter!!.invoke()
        }

        fun tryGetNextDataAndConsume():NextData<Element,ElementMetaData>
        {
            val result = tryGetNextData()
            nextDataGetter = null
            metaDataGetter = {result.elementMetaData}
            return result
        }

        fun tryGetMetaDataOfLastConsumedNextData():ElementMetaData
        {
            val result = metaDataGetter()
            metaDataGetter = {throw IllegalStateException("the remove method has already been called after the last call to the next method")}
            return result
        }

        fun done()
        {
            nextDataGetter = {throw NoSuchElementException("no more elements")}
        }

        fun setNext(value:Element,metaData:ElementMetaData)
        {
            nextDataGetter = {NextData(value,metaData)}
        }
    }

    private data class NextData<Element,ElementMetaData>(val element:Element,val elementMetaData:ElementMetaData)
}
