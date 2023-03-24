package com.redcat.stuchat.base

import com.redcat.stuchat.http.NetWorkHelper
import com.liuxe.lib_common.utils.LogUtils
import com.redcat.stuchat.http.NetExceptionHandle
import com.redcat.stuchat.http.remote.RemoteResponse
import com.redcat.stuchat.http.remote.RemoteResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 *  author : liuxe
 *  date : 2021/7/16 1:27 下午
 *  description :
 */
abstract class BaseRepository {

    /**
     * 网络请求
     * @param request SuspendFunction0<RemoteResponse<T>>
     * @return Flow<RemoteResult<RemoteResponse<T>>>
     */
    suspend fun <T> tryCatch(request: suspend () -> RemoteResponse<T>) = flow {
        try {
            val data: RemoteResponse<T> = request()
            //状态码 0 成功 其他都是出现错误
            emit(RemoteResult.Success(data))
            LogUtils.i("Success：${data.msg}")

        } catch (e: Exception) {
            e.printStackTrace()
            val response = NetExceptionHandle.handleException(e)
            emit(RemoteResult.Failure(response))
            LogUtils.i("Exception：${response.code} ${response.msg}")
        }
    }.flowOn(Dispatchers.IO)


    /**
     * 初始化默认的apiService
     */
    inline fun <reified Api> createApi(): Api {
        return NetWorkHelper.provideRetrofit().create(Api::class.java)
    }

}