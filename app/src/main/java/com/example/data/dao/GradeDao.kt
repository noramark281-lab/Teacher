package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudentGradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    @Query("""
        SELECT * FROM student_grades 
        WHERE gradeLevel = :gradeLevel AND semester = :semester AND month = :month AND section = :section AND subject = :subject 
        ORDER BY studentOrder ASC, id ASC
    """)
    fun getStudents(gradeLevel: String, semester: Int, month: String, section: String, subject: String): Flow<List<StudentGradeEntity>>

    @Query("""
        SELECT * FROM student_grades 
        WHERE gradeLevel = :gradeLevel AND semester = :semester 
        ORDER BY month ASC, section ASC, studentOrder ASC
    """)
    fun getAllStudentsForSemester(gradeLevel: String, semester: Int): Flow<List<StudentGradeEntity>>

    @Query("""
        SELECT * FROM student_grades 
        WHERE semester = :semester 
        ORDER BY gradeLevel ASC, month ASC, section ASC, studentOrder ASC
    """)
    fun getAllStudentsForSemesterGlobal(semester: Int): Flow<List<StudentGradeEntity>>

    @Query("""
        SELECT * FROM student_grades 
        WHERE gradeLevel = :gradeLevel 
        ORDER BY semester ASC, month ASC, section ASC, studentOrder ASC
    """)
    fun getAllStudentsForGrade(gradeLevel: String): Flow<List<StudentGradeEntity>>

    @Query("SELECT * FROM student_grades ORDER BY gradeLevel ASC, semester ASC, month ASC, section ASC, studentOrder ASC")
    fun getAllStudents(): Flow<List<StudentGradeEntity>>

    @Query("SELECT DISTINCT month FROM student_grades WHERE gradeLevel = :gradeLevel AND semester = :semester ORDER BY month ASC")
    fun getDistinctMonths(gradeLevel: String, semester: Int): Flow<List<String>>

    @Query("SELECT DISTINCT section FROM student_grades WHERE gradeLevel = :gradeLevel AND semester = :semester ORDER BY section ASC")
    fun getDistinctSections(gradeLevel: String, semester: Int): Flow<List<String>>

    @Query("SELECT DISTINCT subject FROM student_grades WHERE gradeLevel = :gradeLevel AND semester = :semester ORDER BY subject ASC")
    fun getDistinctSubjects(gradeLevel: String, semester: Int): Flow<List<String>>

    @Query("SELECT DISTINCT subject FROM student_grades ORDER BY subject ASC")
    fun getAllDistinctSubjects(): Flow<List<String>>

    @Query("SELECT * FROM student_grades WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): StudentGradeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentGradeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentGradeEntity>)

    @Update
    suspend fun updateStudent(student: StudentGradeEntity)

    @Delete
    suspend fun deleteStudent(student: StudentGradeEntity)

    @Query("DELETE FROM student_grades WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    @Query("UPDATE student_grades SET attendance = 0, homework = 0, oral = 0, written = 0 WHERE id = :id")
    suspend fun clearStudentScores(id: Long)

    @Query("DELETE FROM student_grades WHERE gradeLevel = :gradeLevel AND semester = :semester AND month = :month AND section = :section AND subject = :subject")
    suspend fun deleteMonthSheet(gradeLevel: String, semester: Int, month: String, section: String, subject: String)

    @Query("DELETE FROM student_grades")
    suspend fun clearAllData()
}
