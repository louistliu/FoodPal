function run_test
    set components (string split -- / (status filename))
    set script_dir $components[..-2]

    set server_path (string join "/" $script_dir "run_server.fish")
    set client_path (string join "/" $script_dir "run_client.fish")
    set arg_len (count $argv)

    echo 'Starting Server'
    ghostty -e fish $server_path &
    disown

    if test $arg_len -eq 0
        set instances 2
    else
        set instances $argv[1]
    end

    sleep 4s
    for i in (seq 1 $instances)
        echo (string join " " 'Starting Client' i)
        ghostty -e fish $client_path &
        disown
    end

    # echo 'Starting Client 2'
    # ghostty -e fish $client_path &
    # disown

end
run_test $argv
