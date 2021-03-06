package dev.logickoder.expense_manager.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import dev.logickoder.expense_manager.R
import dev.logickoder.expense_manager.ui.screens.shared.AppBar
import dev.logickoder.expense_manager.ui.screens.shared.AppBarIconButton
import dev.logickoder.expense_manager.ui.screens.shared.Avatar
import dev.logickoder.expense_manager.ui.screens.shared.ImageSelect
import dev.logickoder.expense_manager.ui.screens.shared.input.Input
import dev.logickoder.expense_manager.ui.screens.shared.input.InputState
import dev.logickoder.expense_manager.ui.theme.Theme
import dev.logickoder.expense_manager.ui.theme.primaryPadding
import dev.logickoder.expense_manager.ui.theme.secondaryPadding
import dev.logickoder.expense_manager.utils.collectAsState
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    state: ProfileState,
    goBack: () -> Unit,
    logout: () -> Unit,
    modifier: Modifier = Modifier,
    sectionSpacing: Dp = secondaryPadding(),
) = with(state) {

    if (showGallery.collectAsState().value) {
        ImageSelect(
            onImageSelected = {
                avatar.emit(it)
                showGallery.emit(false)
            }
        )
    }


    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        backgroundColor = Theme.colors.surface,
        topBar = {
            AppBar(
                title = stringResource(id = R.string.profile),
                navigateBack = goBack,
                actions = {
                    AppBarIconButton(
                        icon = Icons.Outlined.Logout,
                        onClick = logout
                    )
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(primaryPadding()),
                verticalArrangement = Arrangement.spacedBy(
                    sectionSpacing,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    Avatar(
                        model = avatar.collectAsState().value,
                        placeholder = rememberVectorPainter(Icons.Outlined.Person),
                        onClick = {
                            showGallery.emit(true)
                        }
                    )
                    Input(
                        title = stringResource(id = R.string.name),
                        state = InputState(
                            value = name.collectAsState().value,
                            error = nameError.collectAsState().value,
                            onValueChanged = name::emit,
                            required = true,
                        )
                    )
                    Input(
                        title = stringResource(id = R.string.job_description),
                        state = InputState(
                            value = jobDescription.collectAsState().value,
                            error = jobDescriptionError.collectAsState().value,
                            onValueChanged = jobDescription::emit,
                            required = true,
                        )
                    )
                    Input(
                        title = stringResource(id = R.string.department),
                        state = InputState(
                            value = department.collectAsState().value,
                            error = departmentError.collectAsState().value,
                            onValueChanged = department::emit,
                            required = true,
                        )
                    )
                    Input(
                        title = stringResource(id = R.string.location),
                        state = InputState(
                            value = location.collectAsState().value,
                            error = locationError.collectAsState().value,
                            onValueChanged = location::emit,
                            required = true,
                        )
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = secondaryPadding()),
                        shape = Theme.shapes.large,
                        content = {
                            Text(stringResource(id = R.string.save).uppercase())
                        },
                        onClick = {
                            coroutineScope.launch {
                                if (save() != null) {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        "Profile updated successfully"
                                    )
                                }
                            }
                        },
                    )
                }
            )
        }
    )
}
