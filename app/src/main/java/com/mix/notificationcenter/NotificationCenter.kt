package com.mix.notificationcenter

import android.os.Handler
import android.os.Looper
import android.util.SparseArray
import java.util.*



/*
@Hovsep
* I am using this NotificationCenter instead of broadcasts.
* of course we can use live data to avoid it.
*
* */
class NotificationObject constructor(val observer: Any, val completition: ((notificationObject: Any?) -> Unit?)? = null)

object NotificationCenter {
    private val TAG: String? = NotificationCenter.javaClass.canonicalName
    private val observers = SparseArray<ArrayList<NotificationObject>>()

    fun postNotificationName(id: NotificationType, args: Any?) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val mainHandler = Handler(Looper.getMainLooper())

            val myRunnable = Runnable {
                _postNotificationName(id, args)
            }

            mainHandler.post(myRunnable)
        } else {
            _postNotificationName(id, args)
        }
    }

    fun postNotificationNameAsync(id: NotificationType, args: Any?) {
        _postNotificationName(id, args)
    }

    private fun _postNotificationName(id: NotificationType, args: Any?) {
        val objects = observers.get(id.ordinal)
        if (objects != null && !objects.isEmpty()) {
            for (notificationObject in objects) {
                notificationObject.completition?.let { completition ->
                    completition(args)
                }
            }
        }
    }

    fun addObserver(observer: Any, completition: ((notificationObject: Any?) -> Unit)? = null, vararg ids: NotificationType) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("addObserver allowed only from MAIN thread")
        }

        for (type in ids) {
            addObserver(observer, type, completition)
        }
    }

    fun addObserver(observer: Any, id: NotificationType, completition: ((notificationObject: Any?) -> Unit)? = null) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("addObserver allowed only from MAIN thread")
        }


        val notObj = NotificationObject(observer, completition)
        var objects: ArrayList<NotificationObject>? = observers.get(id.ordinal)
        if (objects == null) {
            objects = ArrayList()
            observers.put(id.ordinal, objects)
        }
        var isAdd = true
        for (notificationObject in objects) {
            if (notificationObject.observer == observer) {
                isAdd = false
                break
            }
        }
        if (!isAdd) {
            return
        }

        objects.add(notObj)
    }

    fun removeObserver(observer: Any, id: NotificationType) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("removeObserver allowed only from MAIN thread")
        }


        observers.get(id.ordinal)?.let { observers ->
            for (notificationObject in observers) {
                if (notificationObject.observer == observer) {
                    observers.remove(notificationObject)
                    break
                }
            }
        }
    }

    fun removeObserver(observer: Any) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("removeObserver allowed only from MAIN thread")
        }


        for (i in 0 until observers.size()) {
            val key = observers.keyAt(i)
            val objects = observers.get(key)
            for (notificationObject in objects) {
                if (notificationObject.observer == observer) {
                    objects?.remove(notificationObject)
                    break
                }
            }
        }
    }

    enum class NotificationType constructor(val value: Int) {
        DATA_RECEIVED(0);

        companion object {
            private val allValues = values()

            fun fromOrdinal(n: Int): NotificationType {
                return allValues[n]
            }
        }
    }
}

