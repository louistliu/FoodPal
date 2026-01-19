function run_server --argument-names server_path
    echo "Starting server"

    # if server errors, we wait, otherwise, exit
    mvn -pl server/ -am spring-boot:run ||
        sleep 20m & wait
    echo end
end
run_server
