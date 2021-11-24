package com.camerax.app.response

import com.google.gson.annotations.SerializedName

/*
{
    "success": true,
    "data": [
        {
            ...
        }
    ],
    "message": "http://127.0.0.1:8000/storage/"
}
*/
data class Post(

    @field:SerializedName("success")
    val success: String? = null,

    @field:SerializedName("data")
    val data: MutableList<Data?>? = null,

    @field:SerializedName("message")
    val message: String? = null,
)

/*
"id": 8,
"foto": "foto-8.png",
"caption": "ini dari api",
"id_user": 9,
"created_at": "2021-11-24T16:36:48.000000Z",
"updated_at": "2021-11-24T16:36:48.000000Z"
*/
data class Data(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("foto")
    val foto: String? = null,

    @field:SerializedName("caption")
    val caption: String? = null,

    @field:SerializedName("id_user")
    val id_user: Int? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("updated_at")
    val updated_at: String? = null,
)

/*
"caption": "ini dari api",
"foto": "data:image/png;base64,blabla..."
*/
data class SendPhoto(
    @field:SerializedName("caption")
    val caption: String? = null,

    @field:SerializedName("foto")
    val foto: String? = null,
)

data class General(

    @field:SerializedName("success")
    val success: String? = null,

    @field:SerializedName("data")
    val data: String? = null,

    @field:SerializedName("message")
    val message: String? = null,
)