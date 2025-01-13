package at.ac.fhstp.sanriotcg

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.ac.fhstp.sanriotcg.ui.theme.SanrioTCGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SanrioTCGTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CardApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("SanrioTCG", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
    )
}

@Composable
fun AppFooter(navController: NavHostController) {
    NavigationBar(containerColor = Color(0xFF1976D2)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
            label = { Text("Home", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Collection", tint = Color.White) },
            label = { Text("Collection", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("collection") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.MenuBook, contentDescription = "Album", tint = Color.White) },
            label = { Text("Album", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("album") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Card Shop", tint = Color.White) },
            label = { Text("Card Shop", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("shop") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White) },
            label = { Text("Info", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("info") }
        )
    }
}

@Composable
fun CardApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomePage() }
                composable("collection") { CollectionPage() }
                composable("album") { AlbumPage() }
                composable("shop") { ShopPage() }
                composable("info") { InfoPage() }
            }
        }
    }
}

@Composable
fun HomePage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun CollectionPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Collection Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun AlbumPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Album Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun ShopPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Card Shop Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun InfoPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Info Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}
