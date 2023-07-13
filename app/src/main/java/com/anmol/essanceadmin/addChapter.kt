package com.anmol.essanceadmin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.anmol.essanceadmin.databinding.FragmentAddChapterBinding
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class addChapter : Fragment() {
    lateinit var binding:FragmentAddChapterBinding
    var imgFlag2=false
    var imgFlag=false
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding= FragmentAddChapterBinding.inflate(layoutInflater,container,false)
        var databaseReference=FirebaseDatabase.getInstance().getReference(arguments?.getString("class")+"/Chapter")
        binding.getData.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!TextUtils.isEmpty(binding.title.text)&&!TextUtils.isEmpty(binding.chapter.text)){
                    databaseReference.child(binding.title.text.toString()).child("Chapters").child("ch"+binding.chapter.text)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                binding.title2.setText(snapshot.child("title").getValue().toString())
                                binding.url.setText(snapshot.child("url").getValue().toString())
                                binding.duration.setText(snapshot.child("duration").getValue().toString())
                                imgFlag=true
                                imgFlag2=true
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                    databaseReference.child(binding.title.text.toString()).child("Heading").addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            binding.desc.setText(snapshot.child("discription").getValue().toString())
                            binding.type.setText(snapshot.child("type").getValue().toString())
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }else{
                    Toast.makeText(requireContext(),"Something went wrong",LENGTH_SHORT).show()
                }
            }
        })
        binding.create.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(imgFlag && imgFlag2 && !TextUtils.isEmpty(binding.title.text) && !TextUtils.isEmpty(binding.title2.text) && !TextUtils.isEmpty(binding.chapter.text) && !TextUtils.isEmpty(binding.duration.text) && !TextUtils.isEmpty(binding.type.text) && !TextUtils.isEmpty(binding.url.text) &&!TextUtils.isEmpty(binding.desc.text)){
                    databaseReference.child(binding.title.text.toString()).child("Chapters").child("ch"+binding.chapter.text.toString())
                        .child("duration").setValue(binding.duration.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("Chapters").child("ch"+binding.chapter.text.toString())
                        .child("title").setValue(binding.title2.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("Chapters").child("ch"+binding.chapter.text.toString())
                        .child("url").setValue(binding.url.text.toString())

                    databaseReference.child(binding.title.text.toString()).child("Heading")
                        .child("discription").setValue(binding.desc.text.toString())

                    databaseReference.child(binding.title.text.toString()).child("Heading")
                        .child("type").setValue(binding.type.text.toString()).addOnSuccessListener{
                            Toast.makeText(requireContext(),"Chapter added successfully",
                                LENGTH_SHORT).show()
                            imgFlag=false
                            imgFlag2=false
                        }
                        .addOnFailureListener{
                            Toast.makeText(requireContext(),it.message, LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(requireContext(),"something went wrong", LENGTH_SHORT).show()
                }
            }
        })
        binding.update.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                databaseReference.child(binding.title.text.toString()).removeValue().addOnSuccessListener{
                    Toast.makeText(requireContext(),"Chapter removed successfully", LENGTH_SHORT).show()
                }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(),it.message, LENGTH_SHORT).show()
                    }
            }
        })
        val changeImage =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val imgUri = data?.data
                    var storage: StorageReference = FirebaseStorage.getInstance().getReference("chapter/"+binding.title.text.toString())
                    if (imgUri != null) {
                        storage.putFile(imgUri).addOnSuccessListener {
                            storage.downloadUrl.addOnSuccessListener{

                                databaseReference.child(binding.title.text.toString()).child("Heading").child("image").setValue(it.toString())
                                    .addOnSuccessListener{
                                        Toast.makeText(requireContext(),"Image Uploaded Successfully", LENGTH_SHORT).show()
                                        imgFlag=true
                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                                        imgFlag=false
                                    }

                            }
                                .addOnFailureListener{
                                    Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                                }
                        }
                            .addOnFailureListener{
                                Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                            }
                    }
                }
            }
        val changeImage1 =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val imgUri = data?.data
                    var storage: StorageReference = FirebaseStorage.getInstance().getReference("chapter/"+binding.title2.text.toString())
                    if (imgUri != null) {
                        storage.putFile(imgUri).addOnSuccessListener {
                            storage.downloadUrl.addOnSuccessListener{

                                databaseReference.child(binding.title.text.toString()).child("Chapters").child("ch"+binding.chapter.text.toString()).child("img").setValue(it.toString())
                                    .addOnSuccessListener{
                                        Toast.makeText(requireContext(),"Image Uploaded Successfully", LENGTH_SHORT).show()
                                        imgFlag2=true
                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                                        imgFlag2=false
                                    }

                            }
                                .addOnFailureListener{
                                    Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                                }
                        }
                            .addOnFailureListener{
                                Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                            }
                    }
                }
            }
        binding.uploadImg.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                changeImage1.launch(pickImg)
            }
        })
        binding.headingImg.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                changeImage.launch(pickImg)
            }
        })

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            addChapter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}