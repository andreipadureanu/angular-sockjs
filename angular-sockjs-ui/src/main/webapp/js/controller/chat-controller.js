angular.module('angular.bootstrap.sockjs').controller("chatController", function ($scope) {

    $scope.vm = {
        transport: 'websocket',
        connected: false,
        messages: [],
        input: ''
    };

    $scope.messageConsole = document.getElementById("messageConsoleBody");

    $scope.$watch('messageConsole.scrollHeight', function () {
            var console = $scope.messageConsole;
            console.scrollTop = console.scrollHeight - console.clientHeight;
        },
        true
    );

    var socket = new SockJS(document.location.toString() + 'ws', {}, {"debug": false, "devel": false});
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        $scope.$apply(function () {
            $scope.vm.connected = true;
            $scope.vm.content = 'SockJS connected using ' + $scope.vm.transport;
        });
        stompClient.subscribe('/topic/chatMessages', function (messageContent) {
            var message = JSON.parse(messageContent.body);
            $scope.$apply(function () {
                if (!$scope.vm.logged && $scope.vm.name) {
                    $scope.vm.logged = true;
                } else {
                    $scope.vm.messages.push(message);
                }
            });
        });
    });

    $scope.$on("$destroy", function () {
        if (stompClient != null) {
            stompClient.disconnect(function () {
                console.log('SockJS disconnected...');
            });
        }
    });

    $scope.inputKeyDown = function ($event) {
        var message = $scope.vm.input;
        if (message && message.length > 0 && $event.keyCode === 13) {
            // First message is always the author's name
            if (!$scope.vm.name) {
                $scope.vm.name = message;
            }
            stompClient.send("/chat", {}, JSON.stringify({author: $scope.vm.name, message: message}));
            $scope.vm.input = '';
        }
    };


    /*
    request.onReopen = function (response) {
        $scope.model.connected = true;
        $scope.model.content = 'Atmosphere re-connected using ' + response.transport;
    };
    //For demonstration of how you can customize the fallbackTransport using the onTransportFailure function
    request.onTransportFailure = function (errorMsg, request) {
        atmosphere.util.info(errorMsg);
        request.fallbackTransport = 'long-polling';
        $scope.model.header = 'Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport;
    };
    request.onClose = function (response) {
        $scope.model.connected = false;
        $scope.model.content = 'Server closed the connection after a timeout';
        socket.push(atmosphere.util.stringifyJSON({author: $scope.model.name, message: 'disconnecting'}));
    };
    request.onError = function (response) {
        $scope.model.content = "Sorry, but there's some problem with your socket or the server is down";
        $scope.model.logged = false;
    };
    request.onReconnect = function (request, response) {
        $scope.model.content = 'Connection lost. Trying to reconnect ' + request.reconnectInterval;
        $scope.model.connected = false;
    };
*/
});