package com.example.files

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.net.URLConnection


class AdapterMain(private val data :ArrayList<File>, val fileEvent: FileEvent):RecyclerView.Adapter<AdapterMain.ViewHolderMain>() {
    var ourViewType = 0

    inner class ViewHolderMain(itemView:View):RecyclerView.ViewHolder(itemView){

        val txt = itemView.findViewById<TextView>(R.id.txtNameItem)
        val img = itemView.findViewById<ImageView>(R.id.imgItemFile)

        fun bindData(item:File){

            var fileType = ""
            txt.text = item.name

            if (item.isDirectory){
                img.setImageResource(R.drawable.ic_folder)
            }else{
                when{
                    recognizeFile(item.path, "audio")->{
                        img.setImageResource(R.drawable.ic_audio)
                        fileType = "audio/*"
                    }
                    recognizeFile(item.path, "video")->{
                        img.setImageResource(R.drawable.ic_video)
                        fileType = "video/*"
                    }
                    recognizeFile(item.path, "image")->{
                        img.setImageResource(R.drawable.ic_image)
                        fileType = "image/*"
                    }
                    recognizeFile(item.path, "text")->{
                        img.setImageResource(R.drawable.ic_file)
                        fileType = "text/*"
                    }
                    recognizeFile(item.path, "application/zip")->{
                        img.setImageResource(R.drawable.ic_zip)
                        fileType = "application/zip"
                    }
                    recognizeFile(item.path, "application/pdf")->{
                        img.setImageResource(R.drawable.ic_pdf)
                        fileType = "application/pdf"
                    }
                    isWord(item.path)->{
                        img.setImageResource(R.drawable.ic_word)
                        fileType = "application/msword"
                    }
                    isExel(item.path)->{
                        img.setImageResource(R.drawable.ic_exel)
                        fileType = "application/vnd.ms-excel"
                    }
                    recognizeFile(item.path, "application/mspowerpoint")->{
                        img.setImageResource(R.drawable.ic_powerpoint)
                        fileType = "application/mspowerpoint"
                    }
                }
            }

            itemView.setOnClickListener {
                if (item.isDirectory){
                    fileEvent.onFolderClicked(item.path)
                }else{
                    fileEvent.onFileClicked(item, fileType)
                }
            }
            itemView.setOnLongClickListener {
                fileEvent.onItemLongClicked(item, adapterPosition)
                true
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {
        val view : View
        if (viewType==0){
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_files_linear, parent, false)
        }else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_files_grid, parent, false)
        }
        return ViewHolderMain(view)
    }
    override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
        holder.bindData(data[position])
    }
    override fun getItemCount(): Int {
        return data.size
    }
    override fun getItemViewType(position: Int): Int {
        return ourViewType
    }

    fun recognizeFile(path: String?, type :String): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith(type)
    }
    fun isWord(path: String):Boolean{
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("application/msword") || path.contains(".docx")
    }
    fun isExel(path: String):Boolean{
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("application/vnd.ms-excel") || path.contains(".xlsx")
    }

    fun addFile(newFile: File){
        data.add(0, newFile)
        notifyItemInserted(0)
    }
    fun deleteFile(oldFile: File, position: Int){
        data.remove(oldFile)
        notifyItemRemoved(position)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun changeViewType(newViewType : Int){
        ourViewType = newViewType
        notifyDataSetChanged()
    }

    interface FileEvent{
        fun onFileClicked(file: File, type: String)
        fun onFolderClicked(path: String)
        fun onItemLongClicked(file: File, position: Int)
    }
}