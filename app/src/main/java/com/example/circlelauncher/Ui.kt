package com.example.circlelauncher

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.AlarmClock
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

val Palette = listOf(
    Color(0xFF85B7EB), Color(0xFF97C459), Color(0xFFF0997B), Color(0xFFAFA9EC),
    Color(0xFF5DCAA5), Color(0xFFED93B1), Color(0xFFEF9F27)
)

val IconChoices: List<Pair<String, ImageVector>> = listOf(
    "star" to Icons.Filled.Star,
    "heart" to Icons.Filled.Favorite,
    "home" to Icons.Filled.Home,
    "person" to Icons.Filled.Person,
    "settings" to Icons.Filled.Settings,
    "mail" to Icons.Filled.Email,
    "call" to Icons.Filled.Call,
    "lock" to Icons.Filled.Lock,
    "cart" to Icons.Filled.ShoppingCart,
    "info" to Icons.Filled.Info,
    "bell" to Icons.Filled.Notifications,
    "place" to Icons.Filled.Place,
    "build" to Icons.Filled.Build,
    "face" to Icons.Filled.Face,
    "calendar" to Icons.Filled.DateRange,
    "like" to Icons.Filled.ThumbUp,
    "search" to Icons.Filled.Search,
    "send" to Icons.Filled.Send
)

fun iconFor(key: String): ImageVector? = IconChoices.firstOrNull { it.first == key }?.second

@Composable
fun LauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF141414),
            onSurface = Color.White,
            surfaceContainerHigh = Color(0xFF1A1A1A),
            onSurfaceVariant = Color(0xFFBBBBBB),
            outline = Color(0x66FFFFFF)
        ),
        content = content
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircleButton(
    diameter: Dp,
    color: Color,
    dashed: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val look = if (dashed) {
        Modifier.drawBehind {
            drawCircle(
                color = color,
                radius = this.size.minDimension / 2f - 2f,
                style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f)))
            )
        }
    } else {
        Modifier.border(2.dp, color, CircleShape)
    }
    Box(
        modifier = Modifier
            .size(diameter)
            .clip(CircleShape)
            .then(look)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun AppIcon(pkg: String, diameter: Dp) {
    val ctx = LocalContext.current
    val bmp = remember(pkg) {
        try {
            ctx.packageManager.getApplicationIcon(pkg).toBitmap(128, 128).asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
    if (bmp != null) {
        Image(bitmap = bmp, contentDescription = null, modifier = Modifier.size(diameter))
    }
}

@Composable
fun Clock(onClick: () -> Unit) {
    val now by produceState(initialValue = LocalDateTime.now()) {
        while (true) {
            delay(10_000)
            value = LocalDateTime.now()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(top = 32.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            now.format(DateTimeFormatter.ofPattern("HH:mm")),
            color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Light
        )
        Text(
            now.format(DateTimeFormatter.ofPattern("EEEE d MMMM")),
            color = Color(0xFFAAAAAA), fontSize = 14.sp
        )
    }
}

@Composable
fun GroupBubble(g: Group, modifier: Modifier = Modifier) {
    val color = Palette[g.color % Palette.size]
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val ic = iconFor(g.icon)
            if (ic != null) {
                Icon(ic, contentDescription = g.name, tint = Color.White, modifier = Modifier.size(28.dp))
            } else {
                Text(g.name.take(1).uppercase(), color = Color.White, fontSize = 24.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            g.name, color = Color.White, fontSize = 13.sp, maxLines = 1,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}

@Composable
fun NewCell(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircleButton(68.dp, Color(0x80FFFFFF), dashed = true, onClick = onClick) {
            Text("+", color = Color(0xB3FFFFFF), fontSize = 28.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text("New", color = Color(0xB3FFFFFF), fontSize = 13.sp)
    }
}

@Composable
fun GroupGrid(
    state: LauncherState,
    onOpen: (Long) -> Unit,
    onEdit: (Group) -> Unit,
    onNew: () -> Unit
) {
    val groups = state.groups
    var order by remember { mutableStateOf(groups.map { it.id }) }
    LaunchedEffect(groups) { order = groups.map { it.id } }
    var draggingId by remember { mutableStateOf<Long?>(null) }
    var dragPos by remember { mutableStateOf(Offset.Zero) }
    var moved by remember { mutableStateOf(0f) }
    val openNow by rememberUpdatedState(onOpen)
    val editNow by rememberUpdatedState(onEdit)
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val cols = 3
    val rows = (groups.size + 1 + cols - 1) / cols

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val cellW = maxWidth / cols
        val cellH = 120.dp
        val wPx = with(density) { cellW.toPx() }
        val hPx = with(density) { cellH.toPx() }
        fun slot(i: Int) = Offset((i % cols) * wPx, (i / cols) * hPx)

        Box(Modifier.fillMaxWidth().height(cellH * rows)) {
            order.forEachIndexed { idx, id ->
                val g = groups.firstOrNull { it.id == id }
                if (g != null) {
                    key(id) {
                        val groupNow by rememberUpdatedState(g)
                        val dragging = draggingId == id
                        val target = slot(idx)
                        val ax by animateFloatAsState(target.x, tween(180), label = "x")
                        val ay by animateFloatAsState(target.y, tween(180), label = "y")
                        val px = if (dragging) dragPos.x else ax
                        val py = if (dragging) dragPos.y else ay
                        GroupBubble(
                            g,
                            Modifier
                                .offset { IntOffset(px.roundToInt(), py.roundToInt()) }
                                .size(cellW, cellH)
                                .zIndex(if (dragging) 1f else 0f)
                                .graphicsLayer {
                                    val s = if (dragging) 1.12f else 1f
                                    scaleX = s
                                    scaleY = s
                                }
                                .pointerInput(id, wPx) {
                                    detectTapGestures(onTap = { openNow(id) })
                                }
                                .pointerInput(id, wPx) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggingId = id
                                            dragPos = slot(order.indexOf(id))
                                            moved = 0f
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onDrag = { change, amount ->
                                            change.consume()
                                            dragPos += amount
                                            moved += amount.getDistance()
                                            val col = ((dragPos.x + wPx / 2f) / wPx).toInt().coerceIn(0, cols - 1)
                                            val row = ((dragPos.y + hPx / 2f) / hPx).toInt().coerceAtLeast(0)
                                            val t = (row * cols + col).coerceIn(0, maxOf(0, state.groups.size - 1))
                                            val cur = order.indexOf(id)
                                            if (cur >= 0 && t != cur) {
                                                order = order.toMutableList().apply {
                                                    removeAt(cur)
                                                    add(t, id)
                                                }
                                            }
                                        },
                                        onDragEnd = {
                                            draggingId = null
                                            if (moved < 16f) editNow(groupNow) else state.reorder(order)
                                        },
                                        onDragCancel = {
                                            draggingId = null
                                            order = state.groups.map { it.id }
                                        }
                                    )
                                }
                        )
                    }
                }
            }
            val ns = slot(groups.size)
            NewCell(
                onNew,
                Modifier
                    .offset { IntOffset(ns.x.roundToInt(), ns.y.roundToInt()) }
                    .size(cellW, cellH)
            )
        }
    }
}

@Composable
fun HomeScreen(
    state: LauncherState,
    onOpen: (Long) -> Unit,
    onEdit: (Group) -> Unit,
    onNew: () -> Unit,
    onCall: () -> Unit,
    onOptions: () -> Unit,
    onClock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Clock(onClock)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            GroupGrid(state, onOpen, onEdit, onNew)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleButton(56.dp, Color(0x80FFFFFF), onClick = onCall) {
                Icon(Icons.Filled.Call, contentDescription = "Call", tint = Color.White)
            }
            CircleButton(56.dp, Color(0x80FFFFFF), onClick = onOptions) {
                Icon(Icons.Filled.Settings, contentDescription = "Options", tint = Color.White)
            }
        }
    }
}

@Composable
fun RingScreen(
    group: Group,
    apps: List<AppInfo>,
    onClose: () -> Unit,
    onLaunch: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    val color = Palette[group.color % Palette.size]
    var go by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { go = true }
    val p by animateFloatAsState(if (go) 1f else 0f, tween(350), label = "ring")

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        val radius = minOf(maxWidth, maxHeight).value * 0.36f
        CircleButton(110.dp, color, onClick = onClose) {
            Text(group.name, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
        }
        for (i in 0 until MAX_APPS) {
            val ang = Math.toRadians(-90.0 + i * 45.0)
            val dx = (radius * cos(ang)).toFloat() * p
            val dy = (radius * sin(ang)).toFloat() * p
            val pkg = group.apps.getOrNull(i)
            Column(
                modifier = Modifier
                    .offset(x = dx.dp, y = dy.dp)
                    .alpha(p)
                    .width(76.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (pkg != null) {
                    val label = apps.firstOrNull { it.pkg == pkg }?.label ?: pkg
                    CircleButton(
                        58.dp, color,
                        onClick = { onLaunch(pkg) },
                        onLongClick = { onRemove(pkg) }
                    ) { AppIcon(pkg, 38.dp) }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        label, color = Color.White, fontSize = 11.sp, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center
                    )
                } else {
                    CircleButton(58.dp, Color(0x66FFFFFF), dashed = true, onClick = onAdd) {
                        Text("+", color = Color(0xB3FFFFFF), fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Add", color = Color(0x99FFFFFF), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun GroupDialog(
    initial: Group?,
    onSave: (String, Int, String) -> Unit,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var color by remember { mutableIntStateOf(initial?.color ?: 0) }
    var icon by remember { mutableStateOf(initial?.icon ?: "letter") }
    var error by remember { mutableStateOf(false) }
    val options = listOf("letter") + IconChoices.map { it.first }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New group" else "Edit group", color = Color.White) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(14); error = false },
                    label = { Text("Group name") },
                    isError = error,
                    supportingText = { if (error) Text("Enter a name") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Text("Color", color = Color(0xFFBBBBBB), fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Palette.forEachIndexed { i, c ->
                        Box(
                            Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(if (i == color) 2.dp else 0.dp, Color.White, CircleShape)
                                .clickable { color = i }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Icon", color = Color(0xFFBBBBBB), fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                options.chunked(5).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        row.forEach { key ->
                            val tone = if (key == icon) Color.White else Color(0x55FFFFFF)
                            CircleButton(42.dp, tone, onClick = { icon = key }) {
                                val ic = iconFor(key)
                                if (ic != null) {
                                    Icon(ic, contentDescription = key, tint = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("Aa", color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) error = true else onSave(name.trim(), color, icon)
            }) { Text("Save") }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Delete", color = Color(0xFFF09595)) }
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

@Composable
fun AppPickerDialog(
    apps: List<AppInfo>,
    taken: List<String>,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val shown = apps.filter { it.pkg !in taken && it.label.contains(query, ignoreCase = true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add an app", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.heightIn(max = 340.dp)) {
                    items(shown, key = { it.pkg }) { a ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPick(a.pkg) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(a.pkg, 36.dp)
                            Spacer(Modifier.width(14.dp))
                            Text(a.label, color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun OptionsDialog(onDismiss: () -> Unit, onDefault: () -> Unit, onPhone: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Options", color = Color.White) },
        text = {
            Column {
                TextButton(onClick = onDefault) { Text("Set as default launcher") }
                TextButton(onClick = onPhone) { Text("Phone settings") }
                Text(
                    "Tips: long-press a circle and drag it to reorder, or release without moving to edit it. Long-press an app inside a circle to remove it. Tap the clock to open alarms.",
                    color = Color(0xFFBBBBBB), fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp)
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

private fun safeStart(ctx: Context, intent: Intent) {
    try {
        ctx.startActivity(intent)
    } catch (e: Exception) {
    }
}

@Composable
fun LauncherRoot(
    state: LauncherState,
    apps: List<AppInfo>,
    openId: Long?,
    setOpen: (Long?) -> Unit
) {
    val ctx = LocalContext.current
    var editing by remember { mutableStateOf<Group?>(null) }
    var creating by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    var picking by remember { mutableStateOf(false) }
    var removing by remember { mutableStateOf<String?>(null) }

    val open = state.groups.firstOrNull { it.id == openId }
    BackHandler(enabled = open != null) { setOpen(null) }

    if (open == null) {
        HomeScreen(
            state = state,
            onOpen = { setOpen(it) },
            onEdit = { editing = it },
            onNew = { creating = true },
            onCall = { safeStart(ctx, Intent(Intent.ACTION_DIAL)) },
            onOptions = { showOptions = true },
            onClock = { safeStart(ctx, Intent(AlarmClock.ACTION_SHOW_ALARMS)) }
        )
    } else {
        RingScreen(
            group = open,
            apps = apps,
            onClose = { setOpen(null) },
            onLaunch = { pkg ->
                ctx.packageManager.getLaunchIntentForPackage(pkg)?.let { safeStart(ctx, it) }
            },
            onAdd = { picking = true },
            onRemove = { removing = it }
        )
    }

    if (creating) {
        GroupDialog(
            initial = null,
            onSave = { n, c, i ->
                state.upsert(Group(System.currentTimeMillis(), n, c, i, emptyList()))
                creating = false
            },
            onDelete = null,
            onDismiss = { creating = false }
        )
    }

    editing?.let { g ->
        GroupDialog(
            initial = g,
            onSave = { n, c, i ->
                state.upsert(g.copy(name = n, color = c, icon = i))
                editing = null
            },
            onDelete = {
                state.delete(g.id)
                editing = null
            },
            onDismiss = { editing = null }
        )
    }

    if (showOptions) {
        OptionsDialog(
            onDismiss = { showOptions = false },
            onDefault = {
                safeStart(ctx, Intent(Settings.ACTION_HOME_SETTINGS))
                showOptions = false
            },
            onPhone = {
                safeStart(ctx, Intent(Settings.ACTION_SETTINGS))
                showOptions = false
            }
        )
    }

    if (picking && open != null) {
        AppPickerDialog(
            apps = apps,
            taken = open.apps,
            onPick = { pkg ->
                state.addApp(open.id, pkg)
                picking = false
            },
            onDismiss = { picking = false }
        )
    }

    removing?.let { pkg ->
        if (open != null) {
            val label = apps.firstOrNull { it.pkg == pkg }?.label ?: pkg
            AlertDialog(
                onDismissRequest = { removing = null },
                title = { Text("Remove $label?", color = Color.White) },
                text = { Text("It will only be removed from this circle.", color = Color(0xFFBBBBBB)) },
                confirmButton = {
                    TextButton(onClick = {
                        state.removeApp(open.id, pkg)
                        removing = null
                    }) { Text("Remove") }
                },
                dismissButton = { TextButton(onClick = { removing = null }) { Text("Cancel") } }
            )
        }
    }
}
