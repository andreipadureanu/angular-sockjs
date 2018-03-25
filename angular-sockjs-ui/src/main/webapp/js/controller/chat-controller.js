angular.module('angular.bootstrap.sockjs').controller("chatController", function ($scope) {

    $scope.model = {
        transport: 'websocket',
        connected: false,
        messages: [],
        input: ''
    };

    var socket = new SockJS(document.location.toString() + 'ws', {}, {"debug": false, "devel": false});
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        $scope.$apply(function () {
            $scope.model.connected = true;
            $scope.model.content = 'SockJS connected using ' + $scope.model.transport;
        });
        stompClient.subscribe('/topic/chatMessages', function (messageContent) {
            var message = JSON.parse(messageContent.body);
            $scope.$apply(function () {
                if (!$scope.model.logged && $scope.model.name) {
                    $scope.model.logged = true;
                } else {
                    $scope.model.messages.push(message);
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
        var msg = $scope.model.input;
        if (msg && msg.length > 0 && $event.keyCode === 13) {
            // First message is always the author's name
            if (!$scope.model.name) {
                $scope.model.name = msg;
            }
            stompClient.send("/chat", {}, JSON.stringify({author: $scope.model.name, message: msg}));
            $scope.model.input = '';
        }
    }

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