package com.test.kakaochatpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.kakaochatpractice.ui.theme.KakaochatPracticeTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KakaochatPracticeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ChatRoom()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoom(
) {
    var chatLogList by remember { mutableStateOf(listOf<String>()) }
    var hideKeyboard by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.clickable { hideKeyboard = true },
        containerColor = "#BACEE0".toColor(),
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = "#BACEE0".toColor(),
                    titleContentColor = "#000000".toColor(),
                ),
                title = {
                    Text("이준희")
                }
            )
        }, bottomBar = {
            BottomAppBarCustom(
                hideKeyboard,
                onFocusClear = { hideKeyboard = false },
                chatLogList = chatLogList.toMutableList()
            ) { newList ->
                chatLogList = newList
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            for (item in chatLogList) {
                val delimiter = "="
                val (key, value) = item.split(delimiter, limit = 2)
                item {
                    ChatLog(time = key, text = value)
                }
            }
        }

    }

}


@Composable
fun BottomAppBarCustom(
    hideKeyboard: Boolean = false,
    onFocusClear: () -> Unit = {},
    chatLogList: MutableList<String>,
    onEventClickAction: (List<String>) -> Unit
) {
    var text by remember { mutableStateOf("") } // 사용자가 입력한 문자
    val focusRequester = remember { FocusRequester() }
    val maxLine = 3
    var isFocused by remember { mutableStateOf(!hideKeyboard) }
    var isTyping by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    BottomAppBar(
        containerColor = "#FFFFFF".toColor(),
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.height((24 * (text.count { c -> c == '\n' }).coerceAtMost(maxLine) + 80).dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(8.dp)) {
                TextField(
                    value = text,
                    onValueChange = {
                            text = it
                        isTyping = text.isNotEmpty()
                        },
                    placeholder = { Text("문자를 입력하세요", modifier = Modifier.padding(1.dp)) },
                    keyboardActions = KeyboardActions(onDone = {

                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default,
                        keyboardType = KeyboardType.Text),
                    modifier = Modifier.width(300.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            isFocused = it.isFocused
                        }
                        .focusable(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),

                )
            }
            SendIconButton(chatLogList = chatLogList, value = text, isTyping = isTyping
                , onEventClickAction = {newList ->
                    onEventClickAction(newList)
                    text = ""
                    focusManager.clearFocus()
                    isTyping = false
                })
        }
    }
    if (hideKeyboard) {
        focusManager.clearFocus()
        text = ""
        onFocusClear()
    }
}

@Composable
fun ChatLog(modifier: Modifier = Modifier, time: String, text: String) {
    LazyRow(
        modifier = modifier.padding(),
        verticalAlignment = Alignment.Bottom
    ) {
        item {
            Text(
                text = time, modifier = Modifier.padding(end = 5.dp), fontSize = 10.sp
            )
        }
        item {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background("#FFEB33".toColor())
                    .padding(10.dp)
                    .widthIn(0.dp, 250.dp)
            ) {
                Text(
                    text = text,
                )
            }
        }
    }
}

@Composable
fun SendIconButton(
    modifier: Modifier = Modifier,
    chatLogList: MutableList<String>,
    value: String,
    isTyping: Boolean,
    onEventClickAction: (List<String>) -> Unit
) {
    if(isTyping) {
        IconButton(onClick = {
            chatLogList.add(getCurrentTime() + "=" + value)
            onEventClickAction(chatLogList)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = "Send Icon",
                modifier = modifier
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatRoomPreview() {
    KakaochatPracticeTheme {
        ChatRoom()
    }
}

fun getCurrentTime(): String {
    val currentTime = Calendar.getInstance().time
    val formatter = SimpleDateFormat("a hh:mm", Locale.KOREA)
    return formatter.format(currentTime)

}

fun String.toColor() = Color(android.graphics.Color.parseColor(this))