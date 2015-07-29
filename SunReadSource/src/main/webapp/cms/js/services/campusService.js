var campusServices = angular.module('campusServices', ['ngResource', 'nourConfig']);

campusServices.factory('Campus',['$resource', 'config', '$http',
	function($resource, config, $http){
		var api = {};
		api.Update = function(data, callback){
			$http.put(config.HOST + 'campus/' + data.id, data)
			.success(function(data, status, headers, config){
				callback(data);
			});
		}
		api.Delete = function(id, callback){
			$http.delete(config.HOST + 'campus/' + id)
			.success(function(data, status, headers, config){
				callback(data);
			});
		}
		return api;
	}]);


campusServices.factory('GetCampus', ['$resource', 'config',
	function($resource, config){
		return $resource(config.HOST + "campuss?page=:page&size=:size&sortBy=id",
			{page:'@_page', size:'@_size'}, {}
			);
	}]);