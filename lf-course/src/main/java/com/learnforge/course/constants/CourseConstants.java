package com.learnforge.course.constants;

/**
 * @author wusongsong
 * @since 2022/7/14 13:44
 * @version 1.0.0 1.0
 **/
public class CourseConstants {

    public static final long CATEGORY_ROOT = 0;

    public class CourseStep {
        public static final int BASE_INFO = 1; //Basic information
        public static final int CATALOGUE = 2; //Directory
        public static final int MEDIA = 3; //Video
        public static final int SUBJECT = 4; //Question
        public static final int TEACHER = 5; //Teacher
    }

    //Directory type
    public class CataType{
        public static final int CHAPTER = 1; //Chapter
        public static final int SECTION = 2; //Section
        public static final int PRATICE = 3; //Practice or Test
    }

}
