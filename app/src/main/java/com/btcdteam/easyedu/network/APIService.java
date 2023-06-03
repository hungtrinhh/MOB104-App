package com.btcdteam.easyedu.network;

import com.btcdteam.easyedu.models.Classroom;
import com.btcdteam.easyedu.models.Feedback;
import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Teacher;
import com.btcdteam.easyedu.utils.FCMBodyRequest;
import com.btcdteam.easyedu.utils.SyncBody;
import com.btcdteam.easyedu.utils.UpdateStudentBody;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @POST("teacher")
    Call<JsonObject> teacherRegister(@Body Teacher teacher);

    @POST("auth/login")
    Call<JsonObject> teacherLogin(@Body JsonObject teacher);

    @POST("parent/login")
    Call<JsonObject> parentLogin(@Body JsonObject parent);

    @GET("teacher/login/{email}")
    Call<JsonObject> teacherLoginWithEmail(@Path("email") String email);

    @GET("parent/login/{email}")
    Call<JsonObject> parentLoginWithEmail(@Path("email") String email);

    @POST("classroom")
    Call<JsonObject> createClassroom(@Body Classroom classroom);

    @GET("classroom/teacherId/{teacherId}")
    Call<JsonObject> getListClassroom(@Path("teacherId") int teacherId);

    @GET("classuser/{idClass}")
    Call<JsonObject> getListStudentByIdClassRoom(@Path("idClass") int classId);

    @GET("parent/{idClassroom}")
    Call<JsonObject> getListParentByIdClassRoom(@Path("idClassroom") int classroomId);

    @DELETE("classuser/{idStudent}/{idClass}")
    Call<JsonObject> deleteStudentById(@Path("idStudent") String id, @Path("idClass") int idClass);

    @POST("student/import")
    Call<JsonObject> importStudentData(@Body SyncBody body);

    @GET("parent/phone/{phone}")
    Call<JsonObject> getParentByPhone(@Path("phone") String phone);

    @POST("student")
    Call<JsonObject> createStudentHandMade(@Body JsonObject body);

    @POST("parent")
    Call<JsonObject> createParent(@Body Parent parent);


    @GET("student/{studentId}/{classroomId}")
    Call<JsonObject> getStudentById(@Path("studentId") String studentId, @Path("classroomId") int ClassroomId);

    @PATCH("student")
    Call<JsonObject> updateStudent(@Body UpdateStudentBody updateStudentBody);

    @PATCH("classuser/score/import")
    Call<JsonObject> importStudentScore(@Body RequestBody body);



}
