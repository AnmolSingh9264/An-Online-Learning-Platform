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
import androidx.activity.result.contract.ActivityResultContracts
import com.anmol.essanceadmin.databinding.FragmentBatchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class batch : Fragment() {
   lateinit var binding:FragmentBatchBinding
   var imgFlag = false
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentBatchBinding.inflate(layoutInflater,container,false)

        var databaseReference=FirebaseDatabase.getInstance().getReference(arguments?.getString("class").toString())
        binding.getData.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!TextUtils.isEmpty(binding.batchno.text) && !TextUtils.isEmpty(binding.chapterno.text)){
                    databaseReference.child("B"+binding.batchno.text.toString()).child("Chapters").child("ch"+binding.chapterno.text.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                binding.title.setText(snapshot.child("title").getValue().toString())
                                binding.duration.setText(snapshot.child("duration").getValue().toString())
                                binding.url.setText(snapshot.child("url").getValue().toString())
                                imgFlag=true
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }
            }
        })
        binding.create.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                if(imgFlag && !TextUtils.isEmpty(binding.chapterno.text) && !TextUtils.isEmpty(binding.batchno.text)&&!TextUtils.isEmpty(binding.title.text)&&!TextUtils.isEmpty(binding.duration.text) && !TextUtils.isEmpty(binding.url.text)){
                    databaseReference.child("B"+binding.batchno.text.toString()).child("Chapters").child("ch"+binding.chapterno.text.toString())
                        .child("title").setValue(binding.title.text.toString())
                    databaseReference.child("B"+binding.batchno.text.toString()).child("Chapters").child("ch"+binding.chapterno.text.toString())
                        .child("duration").setValue(binding.duration.text.toString())
                    databaseReference.child("B"+binding.batchno.text.toString()).child("Chapters").child("ch"+binding.chapterno.text.toString())
                        .child("url").setValue(binding.url.text.toString()).addOnSuccessListener{
                            Toast.makeText(requireContext(),"Batch Successfully Created",
                                LENGTH_SHORT).show()
                            imgFlag=false
                        }
                        .addOnFailureListener{
                            Toast.makeText(requireContext(),it.message,
                                LENGTH_SHORT).show()
                        }

                }else{
                    Toast.makeText(requireContext(),"Something went wrong",LENGTH_SHORT).show()
                }
            }
        })
        binding.update.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                databaseReference.child("B"+binding.batchno.text.toString()).removeValue().addOnSuccessListener{
                    Toast.makeText(requireContext(),"Batch removed successfully",
                        LENGTH_SHORT).show()
                }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(),it.message,
                            LENGTH_SHORT).show()
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
                    var storage: StorageReference = FirebaseStorage.getInstance().getReference("batch/"+binding.title.text.toString())
                    if (imgUri != null) {
                        storage.putFile(imgUri).addOnSuccessListener {
                            storage.downloadUrl.addOnSuccessListener{

                                databaseReference.child("B"+binding.batchno.text.toString()).child("Chapters").child("ch"+binding.chapterno.text.toString()).child("img").setValue(it.toString())
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
        binding.uploadImg.setOnClickListener(object :View.OnClickListener{
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
            batch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}