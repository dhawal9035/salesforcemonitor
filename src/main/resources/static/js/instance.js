// This ajax call is used to retrieve all the user data along with the instance selected. THe data is returned in a JSON format.
$(document).ready(function(){
    $.ajax({
    	type: "GET",
    	dataType:"json",
        url: "/getUserData",
        success: function(data)
        { 
        	$.each(data, function(i, item) {
        		console.log(item.user.firstName)
        		$("#dataTable > tbody:last").append("<tr><th scope='row'>"+(i+1)+"</th><td>"+item.user.firstName+"</td><td>"+item.user.lastName+"</td>" +
        				"<td>"+item.user.email+"</td><td>"+item.instances+"</td></tr>");
        	});
        },
    });
});