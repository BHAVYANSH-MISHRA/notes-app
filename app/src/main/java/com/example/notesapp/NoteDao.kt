package com.example.notesapp

import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM Note ORDER BY id DESC")
    fun getAll(): List<Note>
}
