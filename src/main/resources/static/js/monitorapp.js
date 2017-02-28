// The first ajax call is used to retrieve the data from the API and once it is returned, that data is saved to the DB
$(document).ready(function(){
    $.ajax({
    	type: "GET",
    	dataType:"json",
        url: "https://api.status.salesforce.com/v1/instances",
        success: function(data)
        { 
        	//console.log(data[0].key);
        	//console.log(data[1].key);
        	$.each(data, function(i, item) {
        		$("#instanceList").append("<option value="+item.key+">"+item.key+"</option>");
        	});
        	var jsonData = JSON.stringify(data);
        	$.ajax({
            	type: "POST",
            	dataType:"json",
            	data: jsonData,
            	contentType: "application/json; charset=utf-8",
                url: "/saveInstanceData",
                success: function(data)
                { 
                	console.log("Information saved");
                },
            });
        },
    });
});