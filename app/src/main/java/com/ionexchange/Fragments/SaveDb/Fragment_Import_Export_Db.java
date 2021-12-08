package com.ionexchange.Fragments.SaveDb;

import static com.ionexchange.Database.WaterTreatmentDb.DB_NAME;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.DbExportImportBinding;

import java.io.File;

public class Fragment_Import_Export_Db extends Fragment {

    DbExportImportBinding mBinding;
    ApplicationClass mAppClass;
    Context mContext;
    BaseActivity baseActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.db_export_import, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
        baseActivity = (BaseActivity) getActivity();
        mAppClass = (ApplicationClass) getActivity().getApplicationContext();
        mBinding.importDb.setOnClickListener(v -> {
        openFolder();
        });
    }
    public void openFolder() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String currentDBPath = getContext().getDatabasePath(DB_NAME).getAbsolutePath();
            File file = new File(currentDBPath);
            Uri uri = FileProvider.getUriForFile(mContext,mContext.getApplicationContext().getPackageName() + ".provider",
                    file);
            intent.setDataAndType(uri,"application/vnd.sqlite3");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(intent);
        }catch (Exception e){
            Log.e("TAG", "openFolder: "+e );
        }

    }

}
