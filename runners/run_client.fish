function run_client --argument-names server_path
    echo "Starting client"

    # if server errors, we wait, otherwise, exit
    mvn -pl client/ -am javafx:run ||
        sleep 20m & wait
    echo end
end
run_client
