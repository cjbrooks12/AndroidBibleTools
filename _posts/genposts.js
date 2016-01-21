var fs = require('fs');

var count = 1;

for(var year = 2014; year <= 2016; year++) {
	for(var month = 1; month <= 12; month++) {
		for(var day = 1; day <= 28; day++) {

			var filename = year + '-';
			if(month < 10) filename += '0' + month + '-';
			else filename += month + '-';

			if(day < 10) filename += '0' + day + '-';
			else filename += day + '-';

			filename += 'post' + count + '.md';

			var filecontent = '';
			filecontent += '---\n';
			filecontent += 'layout: post\n';
			filecontent += 'title: Post ' + count + '\n';
			filecontent += 'description: Post ' + count + '\n';
			filecontent += '---\n';
			count++;

			fs.writeFile(filename, filecontent, function (err) {
				if (err)
					return console.log(err);
			});
		}
	}
}