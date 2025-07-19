@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LingXi's 2048",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Version 1.1.1",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "Author: LingXi9374",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "https://github.com/LingXi9374",
            style = MaterialTheme.typography.body2,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "Repository: LingXi's-2048-game",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "https://github.com/LingXi9374/LingXi's-2048-game",
            style = MaterialTheme.typography.body2,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}