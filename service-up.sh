#!/bin/bash

DO_BUILD=false
SKIP_TESTS=false

BUILD_TARGET="final-java"
IMAGE_TAG="latest"

BACKEND_DIR="./backend-orders"
FRONTEND_DIR="./frontend-orders"
TF_DIR="$BACKEND_DIR/terraform"

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -b|--build) 
            DO_BUILD=true 
            ;;
        -st|--skip-tests) 
            SKIP_TESTS=true 
            ;;
        -n|--native)
            BUILD_TARGET="final-native"
            echo "🔥 Native Mode activated for Build"
            ;;
        -h|--help)
            echo "Use: ./service-up.sh [options]"
            echo "  -b, --build   Force build of the Docker images before deploying."
            echo "  -st, --skip-tests   Skip running backend tests."
            echo "  -n, --native   Build native image."
            exit 0
            ;;
        *) 
            echo "Unknown option: $1" 
            ;;
    esac
    shift
done

echo "🚀 Starting service deployment script..."

if [[  "$SKIP_TESTS" = false && "$DO_BUILD" = true && "$BUILD_TARGET" = "final-native" ]]; then
    echo "🧪 Testing Backend (this could take a while)... (use --skip-tests if you dont want to run them but the first time is important for native compile in order to register classes)."
    cd $BACKEND_DIR || { echo "❌ Backend directory not found"; exit 1; }
    ./gradlew clean test || { echo "❌ Backend tests failed"; exit 1; }
    cd - > /dev/null || exit 1
    echo "✅ Backend tests passed."

else
    echo "⏩ Skipping tests"
fi

if [ "$DO_BUILD" = true ]; then
    echo "🔨  Building Backend image ($BUILD_TARGET) (this could take a while)..."

    docker build \
        --target $BUILD_TARGET \
        -t backend-orders:$IMAGE_TAG \
        -f $BACKEND_DIR/Dockerfile $BACKEND_DIR \
        || { echo "❌ Build failed"; exit 1; }

    echo "🏗️  Building Frontend image..."
    
    docker build -t frontend-orders:latest -f $FRONTEND_DIR/Dockerfile $FRONTEND_DIR || { echo "❌ Frontend build failed"; exit 1; }
else
    echo "⏩ Skipping build (use --build to force it)."
fi

echo "🚀 Deploying services..."

echo "🛑 Stopping existing services..."

docker compose -f $BACKEND_DIR/compose.yaml down 2>/dev/null &
docker compose -f docker-compose.yml down 2>/dev/null &
wait

echo "🟢 Starting infrastructure services..."
docker compose -f $BACKEND_DIR/compose.yaml --env-file .env up -d

echo "⏳ Waiting 5s for Infra to settle..."
sleep 5

echo "⚙️  Applying Terraform..."
if [ -f "$TF_DIR/terraform.tfstate" ]; then
    rm "$TF_DIR/terraform.tfstate" "$TF_DIR/terraform.tfstate.backup"
fi

terraform -chdir=$TF_DIR init > /dev/null
terraform -chdir=$TF_DIR apply -var-file=local.tfvars -auto-approve

if [ $? -eq 0 ]; then
    echo "✅ Infra ready."
else
    echo "❌ Terraform failed."
    exit 1
fi

echo "🟢 Starting backend and frontend services..."
docker compose -f docker-compose.yml --env-file .env up -d

echo "🎉 All services are up and running!"

echo "You can access the frontend at http://localhost:8099"