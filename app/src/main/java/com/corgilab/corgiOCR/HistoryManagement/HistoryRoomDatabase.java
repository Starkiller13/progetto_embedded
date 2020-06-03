package com.corgilab.corgiOCR.HistoryManagement;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities= {History.class}, version=1, exportSchema=false)
public abstract class HistoryRoomDatabase extends RoomDatabase {
    public abstract HistoryDao historyDao();

    private static volatile HistoryRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static HistoryRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            //per non creare due istanze database contemporaneamente
            synchronized (HistoryRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HistoryRoomDatabase.class, "history_database")
                            //.addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    /*private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                HistoryDao dao = INSTANCE.historyDao();
                dao.deleteAll();
                History word = new History("Hello");
                dao.insert(word);
                word = new History("World");
                dao.insert(word);
            });
        }
    };*/


}