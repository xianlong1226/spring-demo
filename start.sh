#!/bin/bash
# Spring Boot Demo 一键启动脚本
# 用法：
#   ./start.sh          # 启动 thrift-api + app
#   ./start.sh app      # 只启动主应用
#   ./start.sh stop     # 停止所有服务

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$PROJECT_DIR/logs"
PID_DIR="$PROJECT_DIR/.pids"

mkdir -p "$LOG_DIR" "$PID_DIR"

stop_services() {
    echo "🛑 停止服务..."
    for pid_file in "$PID_DIR"/*.pid; do
        if [ -f "$pid_file" ]; then
            local name=$(basename "$pid_file" .pid)
            local pid=$(cat "$pid_file")
            if kill -0 "$pid" 2>/dev/null; then
                kill "$pid"
                echo "  ✅ $name (PID: $pid) 已停止"
            else
                echo "  ⚠️  $name (PID: $pid) 已不在运行"
            fi
            rm "$pid_file"
        fi
    done
}

start_thrift_api() {
    echo "🚀 启动 thrift-api (RPC 服务端, 端口 9090)..."
    cd "$PROJECT_DIR/thrift-api"
    ../gradlew bootRun > "$LOG_DIR/thrift-api.log" 2>&1 &
    local pid=$!
    echo "$pid" > "$PID_DIR/thrift-api.pid"

    # 等待 9090 端口就绪
    echo "  ⏳ 等待 Thrift Server 就绪..."
    for i in $(seq 1 30); do
        if grep -q "Thrift Server 启动" "$LOG_DIR/thrift-api.log" 2>/dev/null; then
            echo "  ✅ thrift-api 已启动 (PID: $pid)"
            return 0
        fi
        sleep 1
    done
    echo "  ⚠️  thrift-api 启动超时，请检查 logs/thrift-api.log"
    return 1
}

start_app() {
    echo "🚀 启动 app (主应用, 端口 8080)..."
    cd "$PROJECT_DIR"
    ./gradlew bootRun > "$LOG_DIR/app.log" 2>&1 &
    local pid=$!
    echo "$pid" > "$PID_DIR/app.pid"

    # 等待 8080 端口就绪
    echo "  ⏳ 等待主应用就绪..."
    for i in $(seq 1 30); do
        if grep -q "Started Application" "$LOG_DIR/app.log" 2>/dev/null; then
            echo "  ✅ app 已启动 (PID: $pid)"
            return 0
        fi
        sleep 1
    done
    echo "  ⚠️  app 启动超时，请检查 logs/app.log"
    return 1
}

case "${1:-all}" in
    stop)
        stop_services
        ;;
    app)
        echo "📦 只启动主应用（不启动 RPC）"
        start_app
        echo ""
        echo "验证: curl http://localhost:8080/api/health"
        ;;
    all|"")
        echo "📦 启动 thrift-api + app"
        start_thrift_api && start_app
        echo ""
        echo "验证:"
        echo "  主应用: curl http://localhost:8080/api/health"
        echo "  RPC:    curl http://localhost:8080/api/thrift/users/list"
        echo ""
        echo "停止: ./start.sh stop"
        ;;
    *)
        echo "用法: $0 [all|app|stop]"
        exit 1
        ;;
esac
