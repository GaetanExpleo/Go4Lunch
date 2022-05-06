package com.openclassrooms.go4lunch.notification;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.ListenableWorker.Result;
import androidx.work.testing.TestWorkerBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class NotificationWorkerTest {
    private Context mContext;
    private Executor mExecutor;

    @Before
    public void setUp() throws Exception {
        mContext = ApplicationProvider.getApplicationContext();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    @Test
    public void doWork() {
        NotificationWorker worker =
                TestWorkerBuilder.from(mContext, NotificationWorker.class, mExecutor)
                        .build();

        Result result = worker.doWork();
        assertThat(result, is(Result.success()));
    }
}