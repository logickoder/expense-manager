package dev.logickoder.expensemanager.ui.screens.profile

import dev.logickoder.expensemanager.app.data.model.User
import dev.logickoder.expensemanager.app.data.repository.UserRepository
import dev.logickoder.expensemanager.app.state.FormState
import dev.logickoder.expensemanager.app.state.MutableObservableState
import dev.logickoder.expensemanager.app.utils.createErrorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProfileState(
    private val repo: UserRepository,
    scope: CoroutineScope,
) : FormState<User>() {

    private val starting: String? = null

    val nameError = createErrorState()
    val name = MutableObservableState(
        initial = starting,
        update = { it: String?, _ -> it },
        output = { it ?: "" }
    )

    val jobDescriptionError = createErrorState()
    val jobDescription = MutableObservableState(
        initial = starting,
        update = { it: String?, _ -> it },
        output = { it ?: "" }
    )

    val departmentError = createErrorState()
    val department = MutableObservableState(
        initial = starting,
        update = { it: String?, _ -> it },
        output = { it ?: "" }
    )

    val locationError = createErrorState()
    val location = MutableObservableState(
        initial = starting,
        update = { it: String?, _ -> it },
        output = { it ?: "" }
    )

    val avatar = MutableObservableState(
        initial = starting,
        update = { it: String?, _ -> it },
        output = { it }
    )

    init {
        scope.launch {
            repo.get().collect { user ->
                name.emit(user?.name)
                jobDescription.emit(user?.jobDescription)
                department.emit(user?.department)
                location.emit(user?.location)
                avatar.emit(user?.avatar)
            }
        }
    }

    override val errors = listOf(nameError, jobDescriptionError, departmentError, locationError)

    override suspend fun save(): User? {
        val errorMessage = "Please provide a %s"
        clearErrors()

        if (name.value.isNullOrBlank())
            nameError.emit(errorMessage.format("name"))
        if (jobDescription.value.isNullOrBlank())
            jobDescriptionError.emit(errorMessage.format("jobDescription"))
        if (department.value.isNullOrBlank())
            departmentError.emit(errorMessage.format("department"))
        if (location.value.isNullOrBlank())
            locationError.emit(errorMessage.format("location"))

        return if (hasError()) {
            null
        } else User(
            name = name.value!!,
            jobDescription = jobDescription.value!!,
            department = department.value!!,
            location = location.value!!,
            avatar = avatar.value,
        ).also { user ->
            repo.save(user)
        }
    }
}