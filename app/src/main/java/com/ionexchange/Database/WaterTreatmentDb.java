package com.ionexchange.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.ionexchange.Database.Dao.AlarmLogDao;
import com.ionexchange.Database.Dao.CalibrationDao;
import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.DiagnosticDataDao;
import com.ionexchange.Database.Dao.EventLogDao;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Dao.ServicesNotificationDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Dao.TrendDao;
import com.ionexchange.Database.Dao.UserManagementDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.Entity.CalibrationEntity;
import com.ionexchange.Database.Entity.DefaultLayoutConfigurationEntity;
import com.ionexchange.Database.Entity.DiagnosticDataEntity;
import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;
import com.ionexchange.Database.Entity.ServicesNotificationEntity;
import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Database.Entity.TrendEntity;
import com.ionexchange.Database.Entity.UsermanagementEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;

// created by Silambu
@Database(entities = {InputConfigurationEntity.class, OutputConfigurationEntity.class,
        VirtualConfigurationEntity.class, TimerConfigurationEntity.class,
        DefaultLayoutConfigurationEntity.class, MainConfigurationEntity.class,
        KeepAliveCurrentEntity.class, DiagnosticDataEntity.class, UsermanagementEntity.class,
        OutputKeepAliveEntity.class, CalibrationEntity.class, AlarmLogEntity.class,
        EventLogEntity.class, ServicesNotificationEntity.class, TrendEntity.class},
        version = 3, exportSchema = false)
@TypeConverters(Converters.class)

public abstract class WaterTreatmentDb extends RoomDatabase {

    public static volatile WaterTreatmentDb INSTANCE;
    public static final String DB_NAME = "ion_exchange_db.db";

    public static WaterTreatmentDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WaterTreatmentDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WaterTreatmentDb.class, DB_NAME)
                            .addCallback(roomCallback)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.e("SOF", db.getPath());
        }
    };

    public abstract InputConfigurationDao inputConfigurationDao();

    public abstract OutputConfigurationDao outputConfigurationDao();

    public abstract VirtualConfigurationDao virtualConfigurationDao();

    public abstract TimerConfigurationDao timerConfigurationDao();

    public abstract UserManagementDao userManagementDao();

    public abstract DefaultLayoutConfigurationDao defaultLayoutConfigurationDao();

    public abstract MainConfigurationDao mainConfigurationDao();

    public abstract KeepAliveCurrentValueDao keepAliveCurrentValueDao();

    public abstract DiagnosticDataDao diagnosticDataDao();

    public abstract OutputKeepAliveDao outputKeepAliveDao();

    public abstract CalibrationDao calibrationDao();

    public abstract AlarmLogDao alarmLogDao();

    public abstract EventLogDao eventLogDao();

    public abstract ServicesNotificationDao servicesNotificationDao();

    public abstract TrendDao trendDao();

}
