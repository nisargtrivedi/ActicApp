package com.activedistribution.utils


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.activedistribution.model.ProfileData

class PrefStore(private val mAct: Context) {



    private val pref: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(mAct)

    fun cleanPref() {
        val settings = pref
        settings.edit().clear().apply()
    }

    fun containValue(key: String): Boolean {
        val settings = pref
        return settings.contains(key)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val settings = pref
        return settings.getBoolean(key, defaultValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        val settings = pref
        val editor = settings.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun saveString(key: String, value: String) {
        val settings = pref
        val editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }


    fun getString(key: String, defaultVal: String? = null): String? {
        val settings = pref
        return settings.getString(key, defaultVal)
    }


    fun saveLong(key: String, value: Long) {
        val settings = pref
        val editor = settings.edit()
        editor.putLong(key, value)
        editor.apply()
    }


    fun getLong(key: String, defaultVal: Long = 0): Long {
        val settings = pref
        return settings.getLong(key, defaultVal)
    }

    fun setInt(subscription_id: String, sbu_id: Int) {
        val settings = pref
        val editor = settings.edit()
        editor.putInt(subscription_id, sbu_id)
        editor.apply()
    }

    fun getInt(key: String, defaultVal: Int = 0): Int {
        val settings = pref
        return settings.getInt(key, defaultVal)
    }



    fun setProfileData(datas: ProfileData) {
        pref.edit().putString("profile_data", ObjectSerializer.serialize(datas)).commit()
    }

    fun getProfileData(): ProfileData {
        return ObjectSerializer.deserialize(pref.getString("profile_data", null)) as ProfileData
    }

    /* public <T extends Object> void setData(String value, ArrayList<T> datas) {
        getPref().edit().putString(value, ObjectSerializer.serialize(datas)).commit();
    }

    public <T extends Object> ArrayList<T> getData(String name) {

        return (ArrayList<T>) ObjectSerializer.deserialize(getPref().
                getString(name, ObjectSerializer.serialize(new ArrayList<T>())));
    }

    public void setProfileData(ProfileData datas) {
        getPref().edit().putString("profile_data", ObjectSerializer.serialize(datas)).commit();
    }

    public ProfileData getProfileData() {
        return (ProfileData) ObjectSerializer.deserialize(getPref().
                getString("profile_data", null));
    }





    public <T extends Object> void setDataObject(String value, T datas) {
        getPref().edit().putString(value, ObjectSerializer.serialize((Serializable) datas)).commit();
    }

    public <T extends Object> T getDataObject(String key) {
        return  (T)ObjectSerializer.deserialize(getPref().
                getString(key, null));
    }
*/
}