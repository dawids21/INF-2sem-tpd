$projects = @("water-service", "beans-soap-service", "beans-client-service", "brew-service", "gateway", "coffee-ui")

foreach ($projectDir in $projects) {
    Write-Host "Building Docker image for project: $projectDir"
    Set-Location -Path $projectDir
    .\mvnw spring-boot:build-image -D"spring-boot.build-image.imageName=aznu-coffee/$projectDir" -DskipTests
    Set-Location -Path ..
}
