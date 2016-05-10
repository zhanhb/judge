/*
 * Copyright 2016 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author zhanhb
 */
@Mapper
public interface UserPerferenceMapper {

    @Select("select volume from users where user_id=#{userId}")
    long getVolume(@Param("userId") String userId);

    @Update("update users set volume=#{volumn} where user_id=#{userId}")
    long setVolumn(@Param("userId") String userId, @Param("volumn") long volumn);

    @Select("select style from users where user_id=#{userId}")
    int getStyle(@Param("userId") String userId);

    @Update("update users set style=#{style} where user_id=#{userId}")
    long setStyle(@Param("userId") String userId, @Param("style") int style);

    @Select("select language from users where user_id=#{id}")
    int getLanguage(@Param("id") String str);

    @Update("update users set language=#{language} where user_id=#{id}")
    long setLanguage(@Param("id") String userId, @Param("language") int language);

}
