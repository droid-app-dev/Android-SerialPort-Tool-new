package com.droid_app_dev.serialportcommunication.util;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {
    /**
     *
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> rxIoMain() {    //compose
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     *
     *
     * @param <T>
     * @return
     */
    public static <T> SingleTransformer<T, T> rxSingleIoMain() {    //compose
        return new SingleTransformer<T, T>() {
            @Override
            public SingleSource<T> apply(@io.reactivex.annotations.NonNull Single<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static Scheduler io() {
        return Schedulers.io();
    }

    public static Scheduler main() {
        return AndroidSchedulers.mainThread();
    }
}
