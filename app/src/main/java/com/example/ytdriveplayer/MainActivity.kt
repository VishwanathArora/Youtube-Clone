package com.example.ytdriveplayer

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var urlInput: EditText
    private lateinit var playButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val historyList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        urlInput = findViewById(R.id.url_input)
        playButton = findViewById(R.id.play_button)
        historyRecyclerView = findViewById(R.id.history_recycler_view)

        sharedPreferences = getSharedPreferences("video_history", Context.MODE_PRIVATE)
        loadHistory()

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        historyAdapter = HistoryAdapter(historyList) { url ->
            playVideo(url)
            urlInput.setText(url)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        playButton.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                playVideo(url)
                saveToHistory(url)
            } else {
                Toast.makeText(this, "Please enter a video URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playVideo(url: String) {
        player.stop()
        player.clearMediaItems()
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun loadHistory() {
        val saved = sharedPreferences.getStringSet("history_set", setOf()) ?: setOf()
        historyList.clear()
        historyList.addAll(saved)
    }

    private fun saveToHistory(url: String) {
        if (!historyList.contains(url)) {
            historyList.add(0, url) // add to front
            if (historyList.size > 20) { // keep only last 20
                historyList.removeAt(historyList.size - 1)
            }
            sharedPreferences.edit().putStringSet("history_set", historyList.toSet()).apply()
            historyAdapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}