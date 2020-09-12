//  * Copyright 2016-2020 the original author.All rights reserved. 
//  * Kingstar(honeysoft@126.com) 

Date.prototype.format = function(format) {
	var o = {
		"M+" : this.getMonth() + 1, // month
		"d+" : this.getDate(), // day
		"h+" : this.getHours(), // hour
		"m+" : this.getMinutes(), // minute
		"s+" : this.getSeconds(), // second
		"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
		"S" : this.getMilliseconds()
	// millisecond
	};
	if (/(y+)/.test(format))
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
	return format;
};

function formatDateTime(value) {
	if (value == undefined) {
		return "";
	}
	var now = new Date(value);

	return now.format("yyyy-MM-dd hh:mm:ss");
}

(function($){
    $.fn.selection = function(){
        var s,e,range,stored_range;
        if(this[0].selectionStart == undefined){  //IE
            var selection=document.selection;
            if (this[0].tagName.toLowerCase() != "textarea") {
                var val = this.val();
                range = selection.createRange().duplicate();
                range.moveEnd("character", val.length);
                s = (range.text == "" ? val.length:val.lastIndexOf(range.text));
                range = selection.createRange().duplicate();
                range.moveStart("character", -val.length);
                e = range.text.length;
            }else {
                range = selection.createRange(),
                stored_range = range.duplicate();
                stored_range.moveToElementText(this[0]);
                stored_range.setEndPoint('EndToEnd', range);
                s = stored_range.text.length - range.text.length;
                e = s + range.text.length;
            }
        }else{
            s=this[0].selectionStart,
            e=this[0].selectionEnd;
        }
        var te=this[0].value.substring(s,e);
        //return {start:s,end:e,text:te}
		return te;
    };
})(jQuery);