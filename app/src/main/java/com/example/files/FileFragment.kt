package com.example.files

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.files.databinding.DialogAddFileBinding
import com.example.files.databinding.DialogAddFolderBinding
import com.example.files.databinding.DialogDeleteFileBinding
import com.example.files.databinding.FragmentFileBinding
import java.io.File

class FileFragment(val path : String) :Fragment(), AdapterMain.FileEvent {
    lateinit var binding: FragmentFileBinding
    private lateinit var mainAdapter:AdapterMain

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (MainActivity.ourViewType == 0){
            binding.btnList.setImageResource(R.drawable.ic_grid)
        }else{
            binding.btnList.setImageResource(R.drawable.ic_list)
        }

        val file = File(path)
        binding.txtFileName.text = file.name + ">"

        if (file.isDirectory){

            val listOfFiles = arrayListOf<File>()
            listOfFiles.addAll(file.listFiles()!!)
            listOfFiles.sort()

            mainAdapter = AdapterMain(listOfFiles, this)

            binding.recyclerMain.adapter = mainAdapter
            binding.recyclerMain.layoutManager = GridLayoutManager(context, MainActivity.ourSpan, LinearLayoutManager.VERTICAL, false)
            mainAdapter.changeViewType(MainActivity.ourViewType)

            if (listOfFiles.size >0){
                binding.imgEmptyFile.visibility = View.GONE
                binding.recyclerMain.visibility = View.VISIBLE
            }else{
                binding.imgEmptyFile.visibility = View.VISIBLE
                binding.recyclerMain.visibility = View.GONE
            }
        }

        binding.btnAddFile.setOnClickListener {
            createNewFile()
        }
        binding.btnAddFolder.setOnClickListener {
            createNewFolder()
        }
        binding.btnList.setOnClickListener {
            if (MainActivity.ourViewType == 0){
                MainActivity.ourViewType = 1
                MainActivity.ourSpan = 3

                mainAdapter.changeViewType(MainActivity.ourViewType)
                binding.btnList.setImageResource(R.drawable.ic_list)
                binding.recyclerMain.layoutManager = GridLayoutManager(context, MainActivity.ourSpan, LinearLayoutManager.VERTICAL, false)
            }else{
                MainActivity.ourViewType = 0
                MainActivity.ourSpan = 1
                mainAdapter.changeViewType(MainActivity.ourViewType)
                binding.btnList.setImageResource(R.drawable.ic_grid)
                binding.recyclerMain.layoutManager = GridLayoutManager(context, MainActivity.ourSpan, LinearLayoutManager.VERTICAL, false)
            }
        }

    }

    private fun createNewFolder() {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val addFolderBinding = DialogAddFolderBinding.inflate(layoutInflater)
        dialog.setView(addFolderBinding.root)

        dialog.show()

        addFolderBinding.btnCancelFolder.setOnClickListener {
            dialog.dismiss()
        }
        addFolderBinding.btnConfirmFolder.setOnClickListener {
            val nameNewFolder = addFolderBinding.edtFolderName.text.toString()
            val newFolder = File(path + File.separator + nameNewFolder)
            if (!newFolder.exists()){
                if (newFolder.mkdir()){
                    mainAdapter.addFile(newFolder)
                    binding.recyclerMain.scrollToPosition(0)
                    binding.imgEmptyFile.visibility = View.GONE
                    binding.recyclerMain.visibility = View.VISIBLE
                }
            }


            dialog.dismiss()
        }

    }

    private fun createNewFile() {
        val dialogFile = AlertDialog.Builder(requireContext()).create()
        val addFileBinding = DialogAddFileBinding.inflate(layoutInflater)
        dialogFile.setView(addFileBinding.root)

        dialogFile.show()

        addFileBinding.btnCancelFile.setOnClickListener {
            dialogFile.dismiss()
        }
        addFileBinding.btnConfirmFile.setOnClickListener {
            val nameNewFile = addFileBinding.edtFileName.text.toString()
            val newFile = File(path + File.separator + nameNewFile)
            if (!newFile.exists()){
                if (newFile.createNewFile()){
                    mainAdapter.addFile(newFile)
                    binding.recyclerMain.scrollToPosition(0)
                    binding.imgEmptyFile.visibility = View.GONE
                    binding.recyclerMain.visibility = View.VISIBLE
                }
            }


            dialogFile.dismiss()
        }
    }

    override fun onFileClicked(file: File, type: String) {

        val intent = Intent(Intent.ACTION_VIEW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().packageName + ".provider",
                file
            )
            intent.setDataAndType(fileProvider, type)
        }else{
            intent.setDataAndType(Uri.fromFile(file), type)
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)

    }

    override fun onFolderClicked(path: String) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frameMainContainer, FileFragment(path))
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onItemLongClicked(file: File, position: Int) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val dialogDeleteBinding = DialogDeleteFileBinding.inflate(layoutInflater)

        dialog.setView(dialogDeleteBinding.root)
        dialog.show()

        dialogDeleteBinding.btnCancelFile.setOnClickListener {
            dialog.dismiss()
        }
        dialogDeleteBinding.btnDeleteFile.setOnClickListener {
            if (file.exists()){
                if(file.deleteRecursively()){
                    mainAdapter.deleteFile(file, position)
                }
            }

            dialog.dismiss()
        }
    }


}