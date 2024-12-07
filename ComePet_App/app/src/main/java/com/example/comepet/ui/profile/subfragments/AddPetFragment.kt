package com.example.comepet.ui.profile.subfragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.comepet.R
import com.example.comepet.ui.auth.register.model.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddPetFragment : Fragment() {

    private lateinit var imageViewProfilePet: ImageView
    private lateinit var petNameEditText: EditText
    private lateinit var speciesEditText: EditText
    private lateinit var breedEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var pickDateButton: Button
    private lateinit var dateOfBirthTextView: TextView
    private lateinit var savePetButton: Button
    private lateinit var ChangePetProfilePictureImageView: ImageView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var selectedSex: String = ""

    // For Local Image URI
    private var localImageUri: Uri? = null
    private var isEditMode = false
    private var existingPetId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_pet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFirebase()
        initViews(view)
        setupClickListeners()

        // Check if we're in edit mode
        arguments?.let {
            existingPetId = it.getString("PET_ID")
            if (existingPetId != null) {
                isEditMode = true
                loadExistingPetData(existingPetId!!)
            }
        }
    }

    private fun loadExistingPetData(petId: String) {
        val currentUser = mAuth.currentUser ?: return

        db.collection("users").document(currentUser.uid)
            .collection("pets").document(petId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val pet = documentSnapshot.toObject(Pet::class.java)
                    pet?.let {
                        petNameEditText.setText(it.petName)
                        speciesEditText.setText(it.species)
                        breedEditText.setText(it.breed)
                        descriptionEditText.setText(it.description)
                        dateOfBirthTextView.text = it.dateOfBirth

                        // Set sex selection
                        val buttonMale = view?.findViewById<ImageButton>(R.id.imageButtonPetMale)
                        val buttonFemale = view?.findViewById<ImageButton>(R.id.imageButtonPetFemale)

                        if (it.sex == "M") {
                            buttonMale?.setBackgroundResource(R.drawable.circle_blue)
                            buttonFemale?.setBackgroundResource(R.drawable.circle_white)
                            selectedSex = "M"
                        } else {
                            buttonFemale?.setBackgroundResource(R.drawable.circle_blue)
                            buttonMale?.setBackgroundResource(R.drawable.circle_white)
                            selectedSex = "F"
                        }

                        // Load existing profile picture
                        if (it.profilePicture.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(it.profilePicture)
                                .into(imageViewProfilePet)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load pet data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    private fun initViews(view: View) {
        petNameEditText = view.findViewById(R.id.PetNameEditText)
        speciesEditText = view.findViewById(R.id.SpeciesEditText)
        breedEditText = view.findViewById(R.id.BreedEditText)
        descriptionEditText = view.findViewById(R.id.PetDescriptionEditText)
        pickDateButton = view.findViewById(R.id.Pick_Date_Button)
        dateOfBirthTextView = view.findViewById(R.id.DateOfBirthSelectedDate)
        savePetButton = view.findViewById(R.id.SavePetButton)
        imageViewProfilePet = view.findViewById(R.id.imageViewProfilePet)
        ChangePetProfilePictureImageView = view.findViewById(R.id.ChangePetProfilePictureImageView)
    }

    private fun setupClickListeners() {

        // Set up click listeners for Sex Button
        val buttonMale = view?.findViewById<ImageButton>(R.id.imageButtonPetMale)
        val buttonFemale = view?.findViewById<ImageButton>(R.id.imageButtonPetFemale)

        val defaultBackground = R.drawable.circle_white
        val selectedBackground = R.drawable.circle_blue

        if (buttonMale != null) {
            buttonMale.setOnClickListener {
                selectedSex = "M" // Set selected sex to Male
                buttonMale.setBackgroundResource(selectedBackground)
                if (buttonFemale != null) {
                    buttonFemale.setBackgroundResource(defaultBackground)
                } // Reset Female button
            }
        }

        if (buttonFemale != null) {
            buttonFemale.setOnClickListener {
                selectedSex = "F" // Set selected sex to Female
                buttonFemale.setBackgroundResource(selectedBackground)
                if (buttonMale != null) {
                    buttonMale.setBackgroundResource(defaultBackground)
                } // Reset Male button
            }
        }


        imageViewProfilePet.setOnClickListener {
            openGalleryForPet()
        }

        ChangePetProfilePictureImageView.setOnClickListener {
            openGalleryForPet()
        }


        // Pick Date Button
        pickDateButton.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), { _, selectedYear, monthOfYear, dayOfMonth ->
                dateOfBirthTextView.text = "$selectedYear-${monthOfYear + 1}-$dayOfMonth"
            }, year, month, day)
            dpd.show()
        }

        // Save Button
        savePetButton.setOnClickListener {
            if (validateInput()) {
                savePetData()
                findNavController().navigate(R.id.navigation_add_pet_to_navigation_profile)
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (petNameEditText.text.toString().trim().isEmpty()) {
            petNameEditText.error = "Pet name is required"
            isValid = false
        }

        if (selectedSex.isEmpty()) {
            Toast.makeText(requireContext(), "Please select pet's sex", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Add more validation as needed
        return isValid
    }


    private fun openGalleryForPet() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        petGalleryLauncher.launch(intent)
    }

    private val petGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    // Save locally first
                    saveLocalImage(uri)
                }
            }
        }

    private fun saveLocalImage(imageUri: Uri) {
        try {
            // Decode and compress the image
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)

            // Create a local file to save the image
            val localFile = createLocalImageFile()

            // Save compressed bitmap to local file
            val outputStream = FileOutputStream(localFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            outputStream.close()

            // Update UI with local image
            localImageUri = Uri.fromFile(localFile)
            Glide.with(requireContext())
                .load(localImageUri)
                .into(imageViewProfilePet)

        } catch (e: Exception) {
            Log.e("AddPetFragment", "Error saving local image", e)
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createLocalImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File = requireContext().getExternalFilesDir(null)!!
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
    }

    private fun savePetData() {
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val petName = petNameEditText.text.toString().trim()
        val species = speciesEditText.text.toString().trim()
        val breed = breedEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val dateOfBirth = dateOfBirthTextView.text.toString()

        // Generate a new pet ID if not in edit mode
        val petId = existingPetId ?: db.collection("users").document(userId).collection("pets").document().id

        // Create pet object
        val petData = Pet(
            petId = petId,
            petName = petName,
            sex = selectedSex,
            dateOfBirth = dateOfBirth,
            species = species,
            breed = breed,
            description = description
        )

        // Upload image if local image exists
        localImageUri?.let { uri ->
            uploadPetImageToFirebase(uri, petId) { imageUrl ->
                // Update pet data with image URL
                petData.profilePicture = imageUrl
                savePetToFirestore(userId, petId, petData)
            }
        } ?: run {
            // Save pet data without image
            savePetToFirestore(userId, petId, petData)
        }
    }

    private fun savePetToFirestore(userId: String, petId: String, petData: Pet) {
        db.collection("users").document(userId)
            .collection("pets").document(petId)
            .set(petData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Pet data saved successfully!", Toast.LENGTH_SHORT).show()
                // Clear local image after successful upload
                localImageUri = null
            }
            .addOnFailureListener { e ->
                Log.e("AddPetFragment", "Error saving pet data", e)
                Toast.makeText(requireContext(), "Failed to save pet data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPetImageToFirebase(uri: Uri, petId: String, onSuccess: (String) -> Unit) {
        val currentUser = mAuth.currentUser ?: return

        val userId = currentUser.uid
        val filename = UUID.randomUUID().toString()
        val ref: StorageReference = storage.reference.child("Pet_Picture_Profile/$userId/$filename")

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        onSuccess(downloadUri.toString())
                    }
                    .addOnFailureListener { e ->
                        Log.e("AddPetFragment", "Failed to get download URL", e)
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("AddPetFragment", "Error uploading pet image", e)
                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(petId: String? = null): AddPetFragment {
            val fragment = AddPetFragment()
            petId?.let {
                val args = Bundle()
                args.putString("PET_ID", it)
                fragment.arguments = args
            }
            return fragment
        }
    }
}
