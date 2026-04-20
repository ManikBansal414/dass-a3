package com.atelier.ims.ui.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import com.atelier.ims.ImsViewModel
import com.atelier.ims.data.TimetableEntry
import com.atelier.ims.ui.components.AtelierBottomBar
import com.atelier.ims.ui.components.AtelierCard
import com.atelier.ims.ui.components.AtelierTopBar
import com.atelier.ims.ui.components.PrimaryButton
import com.atelier.ims.ui.components.SecondaryButton
import com.atelier.ims.ui.components.StatusDot
import com.atelier.ims.ui.components.TinyLabel
import com.atelier.ims.ui.theme.AtelierCharcoal
import com.atelier.ims.ui.theme.AtelierClay
import com.atelier.ims.ui.theme.AtelierGreen
import com.atelier.ims.ui.theme.AtelierInk
import com.atelier.ims.ui.theme.AtelierLine
import com.atelier.ims.ui.theme.AtelierMuted
import com.atelier.ims.ui.theme.AtelierPanel
import com.atelier.ims.ui.theme.AtelierPaper
import java.util.UUID
import kotlin.math.roundToInt

private val BuilderDays = listOf("MON", "TUE", "WED", "THU", "FRI")
private val BuilderTimes = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "14:00")

private data class BuilderCourseCard(
    val id: String,
    val title: String,
    val batch: String
)

@Composable
fun TimetableBuilderScreen(
    viewModel: ImsViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val isAdmin = viewModel.isAdmin()

    var selectedTeacher by rememberSaveable { mutableStateOf("Prof. Julian Vance") }
    var selectedBatch by rememberSaveable { mutableStateOf(viewModel.batches.firstOrNull()?.name ?: "Fine Arts Cohort 2024") }
    var weeklyLimit by rememberSaveable { mutableStateOf("3") }
    var facultyLoad by rememberSaveable { mutableStateOf("2") }

    val coursePool = remember(viewModel.subjects, viewModel.batches, viewModel.courses, selectedBatch) {
        val sections = listOf("A", "B", "C")
        val targetBatch = viewModel.batches.firstOrNull { it.name == selectedBatch }
        val fromSubjects = targetBatch?.let { batch ->
            sections.flatMap { section ->
                viewModel.subjects.map { subject ->
                    BuilderCourseCard(
                        id = "${batch.id}-${subject.id}-$section",
                        title = "${subject.name} ($section)",
                        batch = batch.name
                    )
                }
            }
        } ?: emptyList()
        val fromCourses = viewModel.courses.filter { it.batch == selectedBatch }.map { course ->
            BuilderCourseCard(
                id = "course-${course.id}",
                title = course.name,
                batch = course.batch
            )
        }
        fromSubjects + fromCourses
    }

    val slotBounds = remember { mutableStateMapOf<String, Rect>() }
    val cardBounds = remember { mutableStateMapOf<String, Rect>() }
    val draftedPlacements = remember { mutableStateMapOf<String, BuilderCourseCard>() }

    var draggingCardId by remember { mutableStateOf<String?>(null) }
    var dragPointer by remember { mutableStateOf(Offset.Zero) }
    var lastPlacedSlot by rememberSaveable { mutableStateOf<String?>(null) }

    if (!isAdmin) {
        Scaffold(containerColor = AtelierPaper, topBar = {
            AtelierTopBar(title = "Timetable Builder", showBack = true, onBack = onBack, trailing = Icons.Outlined.Save)
        }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AtelierPaper)
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Access restricted", style = MaterialTheme.typography.headlineMedium)
                Text("Only Admin can create or modify timetable entries.", color = AtelierMuted)
                SecondaryButton("Back", Modifier.fillMaxWidth(), onBack)
            }
        }
        return
    }

    Scaffold(
        containerColor = AtelierPaper,
        topBar = {
            AtelierTopBar(
                title = "Timetable Builder",
                showBack = true,
                onBack = onBack,
                trailing = Icons.Outlined.Save
            )
        },
        bottomBar = {
            AtelierBottomBar(
                selected = "schedule",
                onHome = onBack,
                onSchedule = {},
                showSchedule = false
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierPaper)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
            Text("Timetable Builder", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Drag any course card from the pool and drop it into a grid slot. This supports large course lists.",
                color = AtelierMuted
            )
            Spacer(Modifier.height(14.dp))

            BatchSelectionRow(
                batches = viewModel.batches.map { it.name },
                selectedBatch = selectedBatch,
                onSelect = { selectedBatch = it }
            )

            Spacer(Modifier.height(10.dp))

            CoursePoolPanel(
                cards = coursePool,
                draggingCardId = draggingCardId,
                onCardPositioned = { id, rect -> cardBounds[id] = rect },
                onDragStart = { id, touchOffset ->
                    draggingCardId = id
                    val cardTopLeft = cardBounds[id]?.topLeft ?: Offset.Zero
                    dragPointer = cardTopLeft + touchOffset
                },
                onDrag = { dragAmount -> dragPointer += dragAmount },
                onDragEnd = {
                    val cardId = draggingCardId
                    if (cardId != null) {
                        val droppedSlot = slotBounds.entries.firstOrNull { it.value.contains(dragPointer) }?.key
                        if (droppedSlot != null) {
                            val card = coursePool.firstOrNull { it.id == cardId }
                            if (card != null) {
                                draftedPlacements[droppedSlot] = card
                                lastPlacedSlot = droppedSlot
                            }
                        }
                    }
                    draggingCardId = null
                }
            )

            Spacer(Modifier.height(10.dp))
            TinyLabel("Timetable Grid")
            Spacer(Modifier.height(8.dp))
            BuilderGrid(
                draftedPlacements = draftedPlacements,
                teacher = selectedTeacher,
                onCellPositioned = { slot, rect -> slotBounds[slot] = rect }
            )

            Spacer(Modifier.height(10.dp))
            ConstraintPanel(
                teacher = selectedTeacher,
                batch = selectedBatch,
                weeklyLimit = weeklyLimit,
                facultyLoad = facultyLoad,
                onTeacherChange = { selectedTeacher = it },
                onBatchChange = { selectedBatch = it },
                onWeeklyLimitChange = { weeklyLimit = it.filter(Char::isDigit) },
                onFacultyLoadChange = { facultyLoad = it.filter(Char::isDigit) }
            )

            Spacer(Modifier.height(10.dp))
            SessionPreviewForBuilder(
                draftedPlacements = draftedPlacements,
                lastPlacedSlot = lastPlacedSlot,
                teacher = selectedTeacher
            )

            val weekly = weeklyLimit.toIntOrNull() ?: 0
            val load = facultyLoad.toIntOrNull() ?: 0
            if (weekly > 0 && load > weekly) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, contentDescription = null, tint = AtelierClay)
                    Text("  Faculty workload exceeds weekly subject limit.", color = AtelierClay, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))
            PrimaryButton("Save Schedule", Modifier.fillMaxWidth()) {
                if (draftedPlacements.isEmpty()) return@PrimaryButton

                draftedPlacements.forEach { (slot, courseCard) ->
                    val parts = slot.split("/").map { it.trim() }
                    val day = parts.firstOrNull() ?: "WED"
                    val time = parts.getOrNull(1) ?: "10:00"
                    viewModel.addTimetableEntry(
                        TimetableEntry(
                            id = UUID.randomUUID().toString(),
                            day = day,
                            date = dayToDate(day),
                            time = time,
                            endTime = inferEndTime(time),
                            title = courseCard.title,
                            category = courseCard.batch,
                            teacher = selectedTeacher,
                            room = "Grid Studio A",
                            seats = 25
                        )
                    )
                }
                draftedPlacements.clear()
                onSaved()
            }
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Undo Placement", Modifier.fillMaxWidth()) {
                val last = lastPlacedSlot
                if (last != null) {
                    draftedPlacements.remove(last)
                    lastPlacedSlot = draftedPlacements.keys.lastOrNull()
                }
            }
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Clear Draft", Modifier.fillMaxWidth()) {
                draftedPlacements.clear()
                lastPlacedSlot = null
            }
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Cancel", Modifier.fillMaxWidth(), onBack)
            Spacer(Modifier.height(16.dp))
            }

            val draggedCard = coursePool.firstOrNull { it.id == draggingCardId }
            if (draggedCard != null) {
                val hoveredRect = slotBounds.values.firstOrNull { it.contains(dragPointer) }
                val referenceRect = hoveredRect ?: slotBounds.values.firstOrNull()
                FloatingDraggedCard(
                    card = draggedCard,
                    pointer = dragPointer,
                    targetRect = referenceRect,
                    snapToTarget = false
                )
            }
        }
    }
}

@Composable
private fun BatchSelectionRow(
    batches: List<String>,
    selectedBatch: String,
    onSelect: (String) -> Unit
) {
    Column {
        TinyLabel("Build Timetable For", AtelierClay)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            batches.forEach { batch ->
                val selected = batch == selectedBatch
                Text(
                    text = batch,
                    color = if (selected) Color.White else AtelierInk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(if (selected) AtelierCharcoal else Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, if (selected) AtelierCharcoal else AtelierLine, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .pointerInput(batch) {
                            detectTapGestures(onTap = { onSelect(batch) })
                        }
                )
            }
        }
    }
}

@Composable
private fun CoursePoolPanel(
    cards: List<BuilderCourseCard>,
    draggingCardId: String?,
    onCardPositioned: (String, Rect) -> Unit,
    onDragStart: (String, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    AtelierCard(containerColor = Color.White) {
        TinyLabel("Course Pool")
        Spacer(Modifier.height(6.dp))
        Text("Available draggable courses: ${cards.size}", color = AtelierMuted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards, key = { it.id }) { card ->
                val active = draggingCardId == card.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (active) 1f else 0f)
                        .onGloballyPositioned { coordinates ->
                            onCardPositioned(card.id, coordinates.boundsInRoot())
                        }
                        .pointerInput(card.id) {
                            detectDragGestures(
                                onDragStart = { touchOffset -> onDragStart(card.id, touchOffset) },
                                onDragEnd = onDragEnd,
                                onDragCancel = onDragEnd,
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount)
                                }
                            )
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = if (active) AtelierCharcoal else AtelierPanel),
                    border = BorderStroke(1.dp, if (active) AtelierClay else AtelierLine)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(card.title, color = if (active) Color.White else AtelierInk, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(card.batch, color = if (active) Color(0xFFC9C5BD) else AtelierMuted, fontSize = 11.sp)
                        }
                        Text("DRAG", color = if (active) AtelierClay else AtelierMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingDraggedCard(
    card: BuilderCourseCard,
    pointer: Offset,
    targetRect: Rect?,
    snapToTarget: Boolean
) {
    val density = LocalDensity.current
    val targetWidthDp = with(density) { (targetRect?.width ?: 220f).toDp() }
    val targetHeightDp = with(density) { (targetRect?.height ?: 64f).toDp() }
    val popupOffset = if (targetRect != null && snapToTarget) {
        IntOffset(targetRect.left.roundToInt(), targetRect.top.roundToInt())
    } else {
        IntOffset(
            x = (pointer.x - (targetRect?.width ?: 220f) / 2f).roundToInt(),
            y = (pointer.y - (targetRect?.height ?: 64f) / 2f).roundToInt()
        )
    }

    Popup(
        alignment = Alignment.TopStart,
        offset = popupOffset
    ) {
        Card(
            modifier = Modifier
                .width(targetWidthDp)
                .height(targetHeightDp)
                .zIndex(10f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = AtelierCharcoal),
            border = BorderStroke(1.dp, AtelierClay)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(card.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1)
                    Text(card.batch, color = Color(0xFFC9C5BD), fontSize = 9.sp, maxLines = 1)
                }
                Text("DRAG", color = AtelierClay, fontWeight = FontWeight.Bold, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun ConstraintPanel(
    teacher: String,
    batch: String,
    weeklyLimit: String,
    facultyLoad: String,
    onTeacherChange: (String) -> Unit,
    onBatchChange: (String) -> Unit,
    onWeeklyLimitChange: (String) -> Unit,
    onFacultyLoadChange: (String) -> Unit
) {
    AtelierCard(containerColor = Color.White) {
        TinyLabel("Constraint Setup")
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(teacher, onTeacherChange, label = { Text("Assign Teacher") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(batch, onBatchChange, label = { Text("Assigned Batch") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(weeklyLimit, onWeeklyLimitChange, label = { Text("Weekly Subject Limit") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(facultyLoad, onFacultyLoadChange, label = { Text("Faculty Workload") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    }
}

@Composable
private fun BuilderGrid(
    draftedPlacements: Map<String, BuilderCourseCard>,
    teacher: String,
    onCellPositioned: (String, Rect) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AtelierPanel),
        border = BorderStroke(1.dp, AtelierLine)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("", modifier = Modifier.width(48.dp))
                BuilderDays.forEach { day ->
                    Text(day, color = AtelierMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }
            }
            BuilderTimes.forEach { time ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(time, color = AtelierMuted, fontSize = 11.sp, modifier = Modifier.width(48.dp))
                    BuilderDays.forEach { day ->
                        val slot = "$day / $time"
                        val placement = draftedPlacements[slot]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(88.dp)
                                .background(if (placement != null) Color(0xFFE7F4EC) else Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, if (placement != null) AtelierGreen else AtelierLine, RoundedCornerShape(8.dp))
                                .onGloballyPositioned { coordinates ->
                                    onCellPositioned(slot, coordinates.boundsInRoot())
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (placement == null) {
                                Text(
                                    text = "DROP",
                                    color = AtelierMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                PlacedCourseCard(
                                    title = placement.title,
                                    room = "Room 301",
                                    teacher = teacher,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .height(76.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlacedCourseCard(
    title: String,
    room: String,
    teacher: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color(0xFFF4E7DF), RoundedCornerShape(8.dp))
            .padding(0.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxSize()
                .background(AtelierClay)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Text(title, color = AtelierInk, fontWeight = FontWeight.Bold, fontSize = 9.sp, maxLines = 1)
            Text(room, color = AtelierClay, fontSize = 8.sp, maxLines = 1)
            Spacer(Modifier.height(3.dp))
            Text(teacher, color = AtelierMuted, fontSize = 8.sp, maxLines = 1)
        }
    }
}

@Composable
private fun SessionPreviewForBuilder(
    draftedPlacements: Map<String, BuilderCourseCard>,
    lastPlacedSlot: String?,
    teacher: String
) {
    AtelierCard(containerColor = Color(0xFFEFECE5)) {
        TinyLabel("Schedule Preview", AtelierClay)
        Spacer(Modifier.height(8.dp))
        Text("Drafted slots: ${draftedPlacements.size}", color = AtelierInk, fontWeight = FontWeight.Bold)
        Text("Last drop: ${lastPlacedSlot ?: "Not yet"}", color = AtelierMuted)
        Text("Teacher: $teacher", color = AtelierMuted)
        if (draftedPlacements.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            draftedPlacements.entries.take(3).forEach { (slot, card) ->
                Text("- $slot -> ${card.title}", color = AtelierInk, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusDot(if (draftedPlacements.isEmpty()) AtelierMuted else AtelierGreen)
            Text(
                text = if (draftedPlacements.isEmpty()) "  No draft placements yet" else "  Ready to publish for student timetable",
                color = if (draftedPlacements.isEmpty()) AtelierMuted else AtelierGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

private fun dayToDate(day: String): Int {
    return when (day) {
        "MON" -> 12
        "TUE" -> 13
        "WED" -> 14
        "THU" -> 15
        else -> 16
    }
}

private fun inferEndTime(startTime: String): String {
    return when (startTime) {
        "08:00" -> "09:00"
        "09:00" -> "10:00"
        "10:00" -> "11:00"
        "11:00" -> "12:00"
        "12:00" -> "13:00"
        else -> "15:00"
    }
}
