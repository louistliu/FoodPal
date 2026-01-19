function run_test
    set components (string split -- / (status filename))
    set script_dir $components[..-2]

    set server_path (string join "/" $script_dir "run_server.fish")
    set client_path (string join "/" $script_dir "run_client.fish")

    echo 'Starting Server'
    ghostty -e fish $server_path &
    disown

    sleep 4s
    echo 'Starting Client 1'
    ghostty -e fish $client_path &
    disown

    echo 'Starting Client 2'
    ghostty -e fish $client_path &
    disown

end
run_test
