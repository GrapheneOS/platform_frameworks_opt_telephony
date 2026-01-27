package com.android.internal.telephony;

import android.annotation.Nullable;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.ext.AppInfoExtFlag;
import android.ext.PackageId;
import android.os.UserHandle;

import java.util.List;

class InboundSmsHandlerExt {

    @Nullable
    static List<String> processSmsRetrieverMatchedPackage(Context ctx, UserHandle user, String pkgName) {
        PackageManager pm = ctx.getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfoAsUser(pkgName, 0, user);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        if (appInfo.ext().hasFlag(AppInfoExtFlag.HAS_GMSCORE_CLIENT_LIBRARY)) {
            try {
                if (pm.getApplicationInfoAsUser(PackageId.GMS_CORE_NAME, 0, user)
                        .ext().getPackageId() == PackageId.GMS_CORE) {

                    if (pm.checkPermission(android.Manifest.permission.RECEIVE_SMS,
                            PackageId.GMS_CORE_NAME) == PackageManager.PERMISSION_GRANTED) {
                        // GmsCompat: allow GmsCore to read SMS OTP of its clients if GmsCore has
                        // the SMS permission
                        return List.of(pkgName, PackageId.GMS_CORE_NAME);
                    }
                }
            } catch (PackageManager.NameNotFoundException ignored) {}
        }

        return List.of(pkgName);
    }
}
