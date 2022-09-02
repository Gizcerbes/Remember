package com.uogames.remembercards.utils

import androidx.recyclerview.widget.RecyclerView
import java.io.Closeable

abstract class ClosableAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>(), Closeable
