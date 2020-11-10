package com.activedistribution.retrofitManager;

public interface MainAsynListener<T> {

	void onPostSuccess(T result, int flag, boolean isSucess) throws Exception;

	void onPostError(int flag);

}
