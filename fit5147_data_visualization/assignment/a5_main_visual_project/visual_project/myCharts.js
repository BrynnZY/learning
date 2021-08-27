window.onload = function(){
    section1slide1();
    section1slide2();
    section2slide1();
    section2slide2();
    section3slide1();
    section3slide2();
    console.log(document.body.clientWidth);//1852
    console.log(document.body.clientHeight)//1009
    var widthRange = document.body.clientWidth/1852
    var heightRange = document.body.clientHeight/1009

    d3.selectAll("p").style("font-size", (heightRange * widthRange * 150) +"%")
    d3.selectAll("h1").style("font-size", (heightRange * widthRange * 450) +"%")
    d3.selectAll("h2").style("font-size", (heightRange * widthRange * 350) +"%")
    d3.selectAll("h3").style("font-size", (heightRange * widthRange * 150) +"%")
}

function section1slide1(){
    var widthRange = document.body.clientWidth/1852
    var heightRange = document.body.clientHeight/1009
    var trumpColor = '#f0b669';
    var bidenColor = '#dfa197';

    var width = 900 * widthRange;
    var height = 400 * heightRange;
    var padding = { top: 10, right: 30, bottom: 30, left: 60 };
    var svg = d3.select("#lineGraph")
        .attr("id", "chart")
        .append("svg")
        .attr('width', width)
        .attr('height',height)
        .attr('class', "svg");

    var pieWidth = 260 * widthRange;
    var pieHeight = 260 * widthRange;
    var pieMargin = 10;
    var radius = Math.min(pieWidth, pieHeight)/2 - pieMargin;

    var arc = d3.arc().innerRadius(50 * widthRange * heightRange).outerRadius(radius);

    var tweetPieSvg = d3.select("#pieGraph1")
    .attr("id", "chart")
    .append("svg")
    .attr('width', pieWidth)
    .attr('height',pieHeight)
    .append("g")
    .attr("transform", "translate(" + pieWidth / 2 + "," + pieHeight / 2 + ")");

    var likesPieSvg = d3.select("#pieGraph2")
    .attr("id", "chart")
    .append("svg")
    .attr('width', pieWidth)
    .attr('height',pieHeight)
    .append("g")
    .attr("transform", "translate(" + pieWidth / 2 + "," + pieHeight / 2 + ")");

    var retweetPieSvg = d3.select("#pieGraph3")
    .attr("id", "chart")
    .append("svg")
    .attr('width', pieWidth)
    .attr('height',pieHeight)
    .append("g")
    .attr("transform", "translate(" + pieWidth / 2 + "," + pieHeight / 2 + ")");
    
    d3.csv("d3_date_data.csv", function(d){
        return {created_at: d3.timeParse('%Y-%m-%d')(d.created_at), 
            num_tweets: d.num_tweets, 
            candidate: d.candidate,
            num_likes: d.num_likes,
            num_retweet: d.num_retweet}
    }, function(data){
        var tweetCount = { Trump: 0, Biden: 0};
        var likesCount = { Trump: 0, Biden: 0};
        var retweetCount = { Trump: 0, Biden: 0};
        data.forEach(function(d){
            if (d.candidate == 'Trump'){
                tweetCount.Trump = parseInt(tweetCount.Trump) + parseInt(d.num_tweets);
                likesCount.Trump = parseInt(likesCount.Trump) + parseInt(d.num_likes);
                retweetCount.Trump= parseInt(retweetCount.Trump) + parseInt(d.num_retweet);
            }else{
                tweetCount.Biden = parseInt(tweetCount.Biden) + parseInt(d.num_tweets);
                likesCount.Biden = parseInt(likesCount.Biden) + parseInt(d.num_likes);
                retweetCount.Biden = parseInt(retweetCount.Biden) + parseInt(d.num_retweet);
            }
        });

        var pieColor = d3.scaleOrdinal()
        .domain(tweetCount)
        .range([trumpColor, bidenColor])
        var tweetPie = d3.pie()
            .value(function(d) {return d.value;});
        var tweetDataReady = tweetPie(d3.entries(tweetCount));

        var likesDataReady = tweetPie(d3.entries(likesCount));

        var retweetDataReady = tweetPie(d3.entries(retweetCount));

        var g1 = tweetPieSvg.append("g");
        var g2 = likesPieSvg.append("g");
        var g3 = retweetPieSvg.append("g");

        g1.selectAll("path")
        .data(tweetDataReady)
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('fill', function(d){ return(pieColor(d.data.key)) })
        .attr("stroke", "black")
        .style("stroke-width", "2px")
        .style("opacity", 0.7)
        .on("mouseover", function(d,i){
            g1.selectAll("path")
            .style("opacity", function(data, index){
                if (i == index){
                    return 1;
                }else{
                    return 0.3
                }
            })
        })
        .on('mouseout', function(d){
            g1.selectAll("path")
            .style("opacity", 0.7)
        });

        g1.selectAll("text")
        .data(tweetDataReady)
        .enter()
        .append("text")
        .text(function(d) {
            return (d.data.key + ' ' +  d.data.value);})
        .attr("transform", function(d) {
            return "translate(" + arc.centroid(d) + ")";
            })
        .attr("text-anchor", "middle")
        .attr("font-size", "12px")

        g2.selectAll("path")
        .data(likesDataReady)
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('fill', function(d){ return(pieColor(d.data.key)) })
        .attr("stroke", "black")
        .style("stroke-width", "2px")
        .style("opacity", 0.7)
        .on("mouseover", function(d,i){
            g2.selectAll("path")
            .style("opacity", function(data, index){
                if (i == index){
                    return 1;
                }else{
                    return 0.3
                }
            })
        })
        .on('mouseout', function(d){
            g2.selectAll("path")
            .style("opacity", 0.7)
        });

        g2.selectAll("text")
        .data(likesDataReady)
        .enter()
        .append("text")
        .text(function(d) {
            return (d.data.key + ' ' +  d.data.value);})
        .attr("transform", function(d) {
            return "translate(" + arc.centroid(d) + ")";
            })
        .attr("text-anchor", "middle")
        .attr("font-size", "12px")

        g3.selectAll("path")
        .data(retweetDataReady)
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('fill', function(d){ return(pieColor(d.data.key)) })
        .attr("stroke", "black")
        .style("stroke-width", "2px")
        .style("opacity", 0.7)
        .on("mouseover", function(d,i){
            g3.selectAll("path")
            .style("opacity", function(data, index){
                if (i == index){
                    return 1;
                }else{
                    return 0.3
                }
            })
        })
        .on('mouseout', function(d){
            g3.selectAll("path")
            .style("opacity", 0.7)
        });

        g3.selectAll("text")
        .data(retweetDataReady)
        .enter()
        .append("text")
        .text(function(d) {
            return (d.data.key + ' ' +  d.data.value);})
        .attr("transform", function(d) {
            return "translate(" + arc.centroid(d) + ")";
            })
        .attr("text-anchor", "middle")
        .attr("font-size", "12px");


        var sumstat = d3.nest()
            .key(function(d){return d.candidate;})
            .entries(data);
        
        var x = d3.scaleTime()
            .domain(d3.extent(data, function(d){return d.created_at;}))
            .range([0 , width - padding.left - padding.right]);
        var XAxis = d3.axisBottom(x);
        var xAx = svg.append("g")
            .attr('transform', 'translate(' + padding.left + ',' + (height - padding.bottom) + ')')
            .call(XAxis);

        var y = d3.scaleLinear()
            .domain([0, d3.max(data, function(d) {return +d.num_tweets;})])
            .range([height - padding.top - padding.bottom, 0]);
        var yAxis = d3.axisLeft(y).tickFormat(d3.format("d"));
        var yAx = svg.append("g")
            .attr('transform', 'translate(' + padding.left + ',' + padding.top + ')')
            .call(yAxis);

        var res = sumstat.map(function(d){ return d.key }) // list of group names
        var color = d3.scaleOrdinal()
            .domain(res)
            .range([bidenColor,trumpColor])

        function make_x_gridlines() {
        return d3.axisBottom(x)
            .ticks(data.length/2)}

        var grid =  svg.append("g")
        .attr("id", "grid")
        .attr("transform", 'translate(' + padding.left + ',' + (height - padding.top - padding.bottom + 10) + ")")
        .call(make_x_gridlines()
            .tickSize((-height + padding.top + padding.bottom))
            .tickFormat("")
        )

        svg.selectAll(".line")
        .data(sumstat)
        .enter()
        .append("path")
        .attr("class", "lines")
        .attr('transform', 'translate(' + padding.left + ',' + padding.top + ')')
        .attr("fill", "none")
        .attr("stroke", function(d){ return color(d.key)})
        .attr("stroke-width", 1.5)
        .attr("d", function(d){
            return d3.line()
            .x(function(d) {return x(d.created_at);})
            .y(function(d) {return y(+d.num_tweets);})(d.values)
        })
        var data_legend = [
            {
                "candidate" : "Biden",
                "color" : bidenColor
            },
            {
                "candidate" : "Trump",
                "color" : trumpColor
            }
        ];

        var legend = svg.selectAll(".legend")
            .data(data_legend)
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function(d, i) { return "translate(-800," + (i * 20 + 30) + ")"; });
        
            legend.append("rect")
            .attr("x", width - 25)
            // .attr("x", (width / 160) * 157)  
            .attr("y", 8)
            .attr("width", 30)
            .attr("height", 3) 
            .style("fill", function(d){
                return d.color
            });
         
        legend.append("text")
            .attr("x", width + 45)
            // .attr("x", (width / 40) * 39)
            .attr("y", 12)
            .style("text-anchor", "end") 
            .attr("font-size", '12px')
            .attr("fill", function(){
                return '#333'
              })
            .text(function(d) { 
                return d.candidate; })

        //Create the circle that travels along the curve of chart
        var focus = svg
            .append('g')
            .append('circle')
            .style("fill", "none")
            .attr("stroke", "black")
            .attr('r', 9)
            .style("opacity", 0)
        
        // Create the text that travels along the curve of chart
        var focusTextTrump = svg
            .append('g')
            .append('text')
            .style("opacity", 0)
            .attr("text-anchor", "left")
            .attr("alignment-baseline", "middle")
            .style("font-size", '12')
        var focusTextBiden = svg
        .append('g')
        .append('text')
        .style("opacity", 0)
        .attr("text-anchor", "left")
        .attr("alignment-baseline", "middle")
        .style("font-size", '12')

        svg
        .append('rect')
        .style("fill", "none")
        .style("pointer-events", "all")
        .attr('x', '60')
        .attr('width', width - padding.left)
        .attr('height', height - padding.top - padding.bottom)
        .style("stroke-width", "0px")
        .on('mouseover', mouseover)
        .on('mousemove', mousemove)
        .on('mouseout', mouseout);

        function mouseover() {
            focus.style("opacity", 1)
            focusTextTrump.style("opacity",1)
            focusTextBiden.style("opacity",1)
          }
        
        function mousemove() {
        // recover coordinate we need
        var x0 = x.invert(d3.mouse(this)[0] - padding.left);
        //var i = bisect(data, x0, 1);
        x0.setHours(0,0,0,0)
        var x1 = -1;
        var x2 = -1;
        for (var i = 0; i < data.length; i++){
            if(data[i].created_at.getTime() == x0.getTime() && data[i].candidate == 'Trump'){
                x1 = i;
            }
            if(data[i].created_at.getTime() == x0.getTime() && data[i].candidate == 'Biden'){
                x2 = i;
            }
            if(x1 != -1 && x2 != -1){break;}
        }
        focus
            .attr("cx", x(data[x1].created_at) + padding.left)
            .attr("cy", (y(data[x1].num_tweets) + y(data[x2].num_tweets))/2)
        focusTextTrump
            .html("Trump: " + data[x1].num_tweets)
            .attr("x", x(data[x1].created_at) + padding.left - 50)
            .attr("y", y(data[x1].num_tweets))
        focusTextBiden
        .html(" Biden: " + data[x2].num_tweets)
        .attr("x", x(data[x1].created_at) + padding.left - 50)
        .attr("y", y(data[x2].num_tweets) + 10)
        }
        function mouseout() {
        focus.style("opacity", 0)
        focusTextTrump.style("opacity", 0)
        focusTextBiden.style("opacity", 0)
        }

        // var x = d3.extent(data, function(d){return d.created_at;});
        // console.log(x);
        var subset = sumstat[0].values;
        var t = []

        subset.forEach(function (d){
            t.push(d.created_at)
        });
        // console.log(t);

        // // Range
        var sliderRange = d3
        .sliderBottom()
        .min(1)
        .max(25)
        .width(width - padding.left - padding.right)
        .ticks(t.length)
        .default([1, 25])
        .step(1)
        .fill('#a3a37e')
        .on('onchange', (val) => {
            d3.selectAll('.value-range').text('Current Time Range: ' + 
                (t[val[0]-1].getDate() +' / '+ 
                (t[val[0]-1].getMonth() +1)) + ' / ' + 
                (t[val[0]-1].getFullYear()) +
                ' to ' + (t[t.length-1].getDate() +' / '+ 
                (t[val[1]-1].getMonth() +1)) + ' / ' + 
                (t[val[1]-1].getFullYear()));
            x.domain([t[val[0]-1], t[val[1]-1]])
            xAx.transition().duration(1000).call(XAxis);
            //console.log(XAxis);

            var tweetCountTemp = { Trump: 0, Biden: 0};
            var likesCountTemp = { Trump: 0, Biden: 0};
            var retweetCountTemp = { Trump: 0, Biden: 0};

            data.forEach(function(d){
                if (d.created_at.getTime() >= t[val[0]-1].getTime() && d.created_at.getTime() <= t[val[1]-1].getTime()){
                    if (d.candidate == 'Trump'){
                        tweetCountTemp.Trump = parseInt(tweetCountTemp.Trump) + parseInt(d.num_tweets);
                        likesCountTemp.Trump = parseInt(likesCountTemp.Trump) + parseInt(d.num_likes);
                        retweetCountTemp.Trump= parseInt(retweetCountTemp.Trump) + parseInt(d.num_retweet);
                    }else{
                        tweetCountTemp.Biden = parseInt(tweetCountTemp.Biden) + parseInt(d.num_tweets);
                        likesCountTemp.Biden = parseInt(likesCountTemp.Biden) + parseInt(d.num_likes);
                        retweetCountTemp.Biden = parseInt(retweetCountTemp.Biden) + parseInt(d.num_retweet);
                    }
                }
            });
            svg
            .selectAll('.lines')
            .transition()
            .duration(1000)
            .attr("d", function(d){
                return d3.line()
                .x(function(d) {return x(d.created_at);})
                .y(function(d) {return y(+d.num_tweets);})(d.values)
            })

            var path1 = tweetPieSvg.selectAll("path");
            var data0 = path1.data();
            var tweetDataReadyTemp = tweetPie(d3.entries(tweetCountTemp));
            path1 = path1.data(tweetDataReadyTemp);

            path1.enter().append("path")
            .each(function(d, i) { this._current = findNeighborArc(i, data0, tweetDataReadyTemp, key) || d; })
            .attr("fill", function(d) { return color(d.data.region); })

            path1.exit()
                .datum(function(d, i) { return findNeighborArc(i, data1, tweetDataReadyTemp, key) || d; })
                .transition()
                .duration(750)
                .attrTween("d", arcTween)
                .remove();

            path1.transition()
                .duration(750)
                .attrTween("d", arcTween);
            
            g1.selectAll("text")
            .data(tweetDataReadyTemp)
            .text(function(d) {
                return (d.data.key + ' ' +  d.data.value);})
            .attr("transform", function(d) {
                return "translate(" + arc.centroid(d) + ")";
            })

            var path2 = likesPieSvg.selectAll("path");
            var data0 = path2.data();
            var likesDataReadyTemp = tweetPie(d3.entries(likesCountTemp));
            path2 = path2.data(likesDataReadyTemp);

            path2.enter().append("path")
            .each(function(d, i) { this._current = findNeighborArc(i, data0, likesDataReadyTemp, key) || d; })
            .attr("fill", function(d) { return color(d.data.region); })

            path2.exit()
                .datum(function(d, i) { return findNeighborArc(i, data1, likesDataReadyTemp, key) || d; })
                .transition()
                .duration(750)
                .attrTween("d", arcTween)
                .remove();

            path2.transition()
                .duration(750)
                .attrTween("d", arcTween);
            
            g2.selectAll("text")
            .data(likesDataReadyTemp)
            .text(function(d) {
                return (d.data.key + ' ' +  d.data.value);})
            .attr("transform", function(d) {
                return "translate(" + arc.centroid(d) + ")";
            })

            var path3 = retweetPieSvg.selectAll("path");
            var data0 = path3.data();
            var retweetDataReadyTemp = tweetPie(d3.entries(retweetCountTemp));
            path3 = path3.data(retweetDataReadyTemp);

            path3.enter().append("path")
            .each(function(d, i) { this._current = findNeighborArc(i, data0, retweetDataReadyTemp, key) || d; })
            .attr("fill", function(d) { return color(d.data.region); })

            path3.exit()
                .datum(function(d, i) { return findNeighborArc(i, data1, retweetDataReadyTemp, key) || d; })
                .transition()
                .duration(750)
                .attrTween("d", arcTween)
                .remove();

            path3.transition()
                .duration(750)
                .attrTween("d", arcTween);
            
            g3.selectAll("text")
            .data(retweetDataReadyTemp)
            .text(function(d) {
                return (d.data.key + ' ' +  d.data.value);})
            .attr("transform", function(d) {
                return "translate(" + arc.centroid(d) + ")";
            })
        });

        function arcTween(d) {

            var i = d3.interpolate(this._current, d);
          
            this._current = i(0);
          
            return function(t) {
              return arc(i(t))
            }
          
        }

        function key(d) {
            return d.data.candidate;
        }

        function findNeighborArc(i, data0, data1, key) {
            var d;
            if(d = findPreceding(i, data0, data1, key)) {
        
              var obj = cloneObj(d)
              obj.startAngle = d.endAngle;
              return obj;
        
            } else if(d = findFollowing(i, data0, data1, key)) {
        
              var obj = cloneObj(d)
              obj.endAngle = d.startAngle;
              return obj;
        
            }
        
            return null
        }

        // Find the element in data0 that joins the highest preceding element in data1.
        function findPreceding(i, data0, data1, key) {
            var m = data0.length;
            while (--i >= 0) {
            var k = key(data1[i]);
            for (var j = 0; j < m; ++j) {
                if (key(data0[j]) === k) return data0[j];
            }
            }
        }
        
        // Find the element in data0 that joins the lowest following element in data1.
        function findFollowing(i, data0, data1, key) {
            var n = data1.length, m = data0.length;
            while (++i < n) {
            var k = key(data1[i]);
            for (var j = 0; j < m; ++j) {
                if (key(data0[j]) === k) return data0[j];
            }
            }
        }

        function cloneObj(obj) {
            var o = {};
            for(var i in obj) {
              o[i] = obj[i];
            }
            return o;
        }

        var gRange = d3
        .select('#slider-range')
        .append('svg')
        .attr('width', width)
        .attr('height', 50)
        .append('g')
        .attr('transform', 'translate(30,30)');

        gRange.call(sliderRange);

        d3.selectAll('.value-range').text('Current Time Range: ' + 
                (t[0].getDate() +' / '+ 
                (t[0].getMonth() +1)) + ' / ' + 
                (t[0].getFullYear()) +
                ' to ' + (t[t.length-1].getDate() +' / '+ 
                (t[t.length-1].getMonth() +1)) + ' / ' + 
                (t[t.length-1].getFullYear()));
    });
}

function section1slide2(){
    var widthRange = document.body.clientWidth/1852
    var heightRange = document.body.clientHeight/1009

    var trumpColor = '#f0b669';
    var bidenColor = '#dfa197';

    var margin = {top: 30, right: 30, bottom: 70, left: 60},
    width = 1500 * widthRange - margin.left - margin.right,
    height = 600 * heightRange - margin.top - margin.bottom;

    var svg = d3.select("#barGraph")
    .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform",
          "translate(" + margin.left + "," + margin.top + ")");
    
    var dataText = svg.selectAll("text").attr("class", "text");

    // Initialize the X axis
    var x = d3.scaleBand()
    .range([ 0, width ])
    .padding(0.2);
    var xAxis = svg.append("g")
    .attr("transform", "translate(0," + height + ")");

    // Initialize the Y axis
    var y = d3.scaleLinear()
    .range([ height, 0]);
    var yAxis = svg.append("g")
    .attr("class", "myYaxis");
    updatebar();

    var radio = d3.selectAll(".aspect");
    radio.on("change", updatebar);
    var select = document.getElementById("selecter");
    select.onchange = updatebar;

    function updatebar(){
        // For each check box:
        d3.selectAll(".aspect").each(function(d){
            var thisRadio= d3.select(this)._groups[0][0]
            var selection = document.getElementById("selecter").selectedIndex;
            var fillColor = "#69b3a2";
            if (thisRadio.checked == true){
                d3.csv("d3_other_distribution_data.csv", 
                function(data){
                    if (thisRadio.value == 0){
                        x.domain(data.map(function(d){ return d.country;}))
                        if(selection == 0){
                            y.domain([0, data[0].country_total]);
                            fillColor = "#69b3a2";
                        }
                        if(selection == 1){
                            y.domain([0, d3.max(data, function(d) { return +d.country_trump })]);
                            fillColor = trumpColor;
                        }
                        if(selection == 2){
                            y.domain([0, d3.max(data, function(d) { return +d.country_biden })]);
                            fillColor = bidenColor;
                        }
                    }else{
                        x.domain(data.map(function(d){ return d.source;}))
                        if(selection == 0){
                            y.domain([0, data[0].source_total]);
                            fillColor = "#69b3a2";
                        }
                        if(selection == 1){
                            y.domain([0, d3.max(data, function(d) { return +d.source_trump })]);
                            fillColor = trumpColor;
                        }
                        if(selection == 2){
                            y.domain([0, d3.max(data, function(d) { return +d.source_biden })]);
                            fillColor = bidenColor;
                        }
                    }
                    xAxis.transition().duration(1000).call(d3.axisBottom(x));
                    yAxis.transition().duration(1000).call(d3.axisLeft(y));
                    // variable u: map data to existing bars
                    // update bars
                    var u = svg.selectAll("rect")
                    .data(data)
                    // update bars
                    u
                    .enter()
                    .append("rect")
                    .merge(u)
                    .transition()
                    .duration(1000)
                    .attr("x", function(d) {
                        if(thisRadio.value == 0){
                            return x(d.country);  
                        }else{
                            return x(d.source);
                        }
                    })
                    .attr("y", function(d) {
                        if (thisRadio.value == 0){ 
                            if(selection == 0){
                                return y(+d.country_total);
                            }
                            if(selection == 1){
                                return y(+d.country_trump);
                            }
                            if(selection == 2){
                                return y(+d.country_biden);
                            }
                        }else{
                            if(selection == 0){
                                return y(+d.source_total);
                            }
                            if(selection == 1){
                                return y(+d.source_trump);
                            }
                            if(selection == 2){
                                return y(+d.source_biden);
                            }
                        }
                    })
                    .attr("width", x.bandwidth())
                    .attr("height", function(d) {
                        if (thisRadio.value == 0){ 
                            if(selection == 0){
                                return height - y(+d.country_total);
                            }
                            if(selection == 1){
                                return height - y(+d.country_trump);
                            }
                            if(selection == 2){
                                return height - y(+d.country_biden);
                            }
                        }else{
                            if(selection == 0){
                                return height - y(+d.source_total);
                            }
                            if(selection == 1){
                                return height - y(+d.source_trump);
                            }
                            if(selection == 2){
                                return height - y(+d.source_biden);
                            }
                        }
                    })
                    .attr("fill", fillColor)
                })
            }
        })
      }
}

function section2slide1(){
    var widthRange = document.body.clientWidth/1852
    var heightRange = document.body.clientHeight/1009

    // set the dimensions and margins of the graph
    var margin = {top: 10, right: 10, bottom: 10, left: 10},
    width = 850 * widthRange  - margin.left - margin.right,
    height = 650 * heightRange - margin.top - margin.bottom;

    // append the svg object to the body of the page
    var svg = d3.select("#trumpWordCloud").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform",
        "translate(" + margin.left + "," + margin.top + ")");

    d3.csv("d3_trump_wordcloud_data.csv", function(data){
        // Constructs a new cloud layout instance. It run an algorithm to find the position of words that suits your requirements
        var layout = d3.layout.cloud()
        .size([width, height])
        .words(data.map(function(d) { return {text: d.word , count: d.n}; }))
        .padding(5)
        .rotate(function() { return ~~0 })
        .fontSize(15)
        .on("end", draw);
        layout.start();

        // This function takes the output of 'layout' above and draw the words
        // Better not to touch it. To change parameters, play with the 'layout' variable above
        function draw(words) {
            svg
            .append("g")
            .attr("transform", "translate(" + layout.size()[0] / 2 + "," + layout.size()[1] / 2 + ")")
            .selectAll("text")
            .data(words)
            .enter().append("text")
            .style("font-size", function(d) { 
                if(d.count >= 50000){
                return 50;
            }
            if(d.count < 50000 && d.count >= 40000){
                return 35 * widthRange;
            }
            if(d.count < 40000 && d.count >= 20000){
                return 25 * widthRange;
            }
            if(d.count < 20000 && d.count >= 10000){
                return 15 * widthRange;
            }
            if(d.count < 10000 ){
                return 10 * widthRange;
            }
            })
            .attr('fill', function(d) { 
                if(d.count >= 50000){
                return "#ff9eb5";
            }
            if(d.count < 50000 && d.count >= 30000){
                return "#f8ce84"
            }
            if(d.count < 30000 && d.count >= 20000){
                return "#f7dbce"
            }
            if(d.count < 20000 && d.count >= 15000){
                return "#ffa696"
            }
            if(d.count < 15000 && d.count >= 10000){
                return "#89979a"
            }
            if(d.count < 10000 ){
                return "#318b8c"
            }
            })
            .attr("text-anchor", "middle")
            //.attr('font-family', 'Impact')
            .attr('stroke-width', '0')
            .attr("transform", function(d) {
                return "translate(" + [d.x*1.4 * widthRange + 50, d.y*1.2 * heightRange + 30] + ")rotate(" + d.rotate + ")";
            })
            .text(function(d) { return d.text; });
        }
    })

    // append the svg object to the body of the page
    var svgBiden = d3.select("#bidenWordCloud").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform",
        "translate(" + margin.left + "," + margin.top + ")");

    d3.csv("d3_biden_wordcloud_data.csv", function(data){

        // Constructs a new cloud layout instance. It run an algorithm to find the position of words that suits your requirements
        var layout = d3.layout.cloud()
        .size([width, height])
        .words(data.map(function(d) { return {text: d.word, count: d.n}; }))
        .padding(5)
        .fontSize(15)
        .rotate(function() { return ~~0; })
        .on("end", draw);
        layout.start();

        // This function takes the output of 'layout' above and draw the words
        // Better not to touch it. To change parameters, play with the 'layout' variable above
        function draw(words) {
            svgBiden
            .append("g")
            .attr("transform", "translate(" + layout.size()[0] / 2 + "," + layout.size()[1] / 2 + ")")
            .selectAll("text")
            .data(words)
            .enter().append("text")
            .style("font-size", function(d) { 
                if(d.count >= 50000){
                return 50  * widthRange;
            }
            if(d.count < 50000 && d.count >= 40000){
                return 35  * widthRange;
            }
            if(d.count < 40000 && d.count >= 20000){
                return 25  * widthRange;
            }
            if(d.count < 20000 && d.count >= 10000){
                return 15  * widthRange;
            }
            if(d.count < 10000 ){
                return 10  * widthRange;
            }
            })
            .attr('fill', function(d) { 
                if(d.count >= 50000){
                return "#ff9eb5";
            }
            if(d.count < 50000 && d.count >= 30000){
                return "#f8ce84"
            }
            if(d.count < 30000 && d.count >= 20000){
                return "#f7dbce"
            }
            if(d.count < 20000 && d.count >= 15000){
                return "#ffa696"
            }
            if(d.count < 15000 && d.count >= 10000){
                return "#89979a"
            }
            if(d.count < 10000 ){
                return "#318b8c"
            }
            })
            .attr('stroke-width', '0')
            .attr("text-anchor", "middle")
            .attr("transform", function(d) {
                return "translate(" + [d.x*1.4 * widthRange, d.y*1.2 * heightRange + 30] + ")rotate(" + d.rotate + ")";
            })
            .text(function(d) { return d.text; });
        }
    })
}

function section2slide2(){
    var widthRange = document.body.clientWidth/1852
    var heightRange = document.body.clientHeight/1009

    var trumpColor = '#f0b669';
    var bidenColor = '#dfa197';

    var margin = {top: 100, right: 100, bottom: 100, left: 100},
				width = Math.min(800 * widthRange, window.innerWidth - 10) - margin.left - margin.right,
				height = Math.min(width, window.innerHeight - margin.top - margin.bottom - 20);
    
    var color = d3.scaleOrdinal()
    .range([trumpColor,bidenColor,"#00A0B0"]);

    var options = {
        w: width,
        h: height,
        margin: margin,
        maxValue: 800000,
        levels: 8,
        roundStrokes: true,
        color: color
    };

    d3.csv("d3_sentiment_data.csv", function(data_total){
        var data = [[],[]];
        data_total.forEach(function(d){
            data[0].push({axis: d.sentiment, value: d.trump_sum});
            data[1].push({axis: d.sentiment, value: d.biden_sum});
        })
        console.log(data);
        
        var cfg = {
            w: 600,				//Width of the circle
            h: 600,				//Height of the circle
            margin: {top: 20, right: 20, bottom: 20, left: 20}, //The margins of the SVG
            levels: 3,				//How many levels or inner circles should there be drawn
            maxValue: 0, 			//What is the value that the biggest circle will represent
            labelFactor: 1.25, 	//How much farther than the radius of the outer circle should the labels be placed
            wrapWidth: 60, 		//The number of pixels after which a label needs to be given a new line
            opacityArea: 0.35, 	//The opacity of the area of the blob
            dotRadius: 4, 			//The size of the colored circles of each blog
            opacityCircles: 0.1, 	//The opacity of the circles of each blob
            strokeWidth: 2, 		//The width of the stroke around each blob
            roundStrokes: false,	//If true the area and stroke will follow a round path (cardinal-closed)
            //color: d3.scaleCategory10()	//Color function
            };

        
            //Put all of the options into a variable called cfg
            if('undefined' !== typeof options){
                for(var i in options){
                  if('undefined' !== typeof options[i]){ cfg[i] = options[i]; }
                }//for i
            }//if

            var maxValue = cfg.maxValue;
        
            var allAxis = (data[0].map(function(i, j){return i.axis})),	//Names of each axis
                total = allAxis.length,					//The number of different axes
                radius = Math.min(cfg.w/2, cfg.h/2), 	//Radius of the outermost circle
                Format = d3.format('%'),			 	//Percentage formatting
                angleSlice = Math.PI * 2 / total;		//The width in radians of each "slice"
            
            //Scale for the radius
            var rScale = d3.scaleLinear()
                .range([0, radius])
                .domain([0, maxValue]);
        
            //Initiate the radar chart SVG
            var svg = d3.select('#RadarGraph').append("svg")
            .attr("width",  cfg.w + cfg.margin.left + cfg.margin.right)
            .attr("height", cfg.h + cfg.margin.top + cfg.margin.bottom)
            .attr("class", "radar");
            //Append a g element		
            var g = svg.append("g")
            .attr("transform", "translate(" + (cfg.w/2 + cfg.margin.left) + "," + (cfg.h/2 + cfg.margin.top) + ")");
        
            var filter = g.append('defs').append('filter').attr('id','glow'),
                feGaussianBlur = filter.append('feGaussianBlur').attr('stdDeviation','2.5').attr('result','coloredBlur'),
                feMerge = filter.append('feMerge'),
                feMergeNode_1 = feMerge.append('feMergeNode').attr('in','coloredBlur'),
                feMergeNode_2 = feMerge.append('feMergeNode').attr('in','SourceGraphic');
        
            //Wrapper for the grid & axes
            var axisGrid = g.append("g").attr("class", "axisWrapper");
            
            //Draw the background circles
            axisGrid.selectAll(".levels")
               .data(d3.range(1,(cfg.levels+1)).reverse())
               .enter()
                .append("circle")
                .attr("class", "gridCircle")
                .attr("r", function(d, i){return radius/cfg.levels*d;})
                .style("fill", "#CDCDCD")
                .style("stroke", "#CDCDCD")
                .style("fill-opacity", cfg.opacityCircles)
                .style("filter" , "url(#glow)");
        
            //Text indicating at what each level is
            axisGrid.selectAll(".axisLabel")
               .data(d3.range(1,(cfg.levels+1)).reverse())
               .enter().append("text")
               .attr("class", "axisLabel")
               .attr("x", 4)
               .attr("y", function(d){return -d*radius/cfg.levels;})
               .attr("dy", "0.4em")
               .style("font-size", "10px")
               .attr("fill", "#737373")
               .text(function(d,i) { return maxValue * d/cfg.levels; });

               var axis = axisGrid.selectAll(".axis")
               .data(allAxis)
               .enter()
               .append("g")
               .attr("class", "axis");
           //Append the lines
           axis.append("line")
               .attr("x1", 0)
               .attr("y1", 0)
               .attr("x2", function(d, i){ return rScale(maxValue*1.1) * Math.cos(angleSlice*i - Math.PI/2); })
               .attr("y2", function(d, i){ return rScale(maxValue*1.1) * Math.sin(angleSlice*i - Math.PI/2); })
               .attr("class", "line")
               .style("stroke", "white")
               .style("stroke-width", "2px");
       
           //Append the labels at each axis
           axis.append("text")
               .attr("class", "legend")
               .style("font-size", "11px")
               .attr("text-anchor", "middle")
               .attr("dy", "0.35em")
               .attr("x", function(d, i){ return rScale(maxValue * cfg.labelFactor) * Math.cos(angleSlice*i - Math.PI/2); })
               .attr("y", function(d, i){ return rScale(maxValue * cfg.labelFactor) * Math.sin(angleSlice*i - Math.PI/2); })
               .text(function(d){return d})
               .call(wrap, cfg.wrapWidth);

            //The radial line function
            var radarLine = d3.lineRadial()
            //.interpolate("linear-closed")
            .radius(function(d) { return rScale(d.value); })
            .angle(function(d,i) {	return i*angleSlice; });
                
            // if(cfg.roundStrokes) {
            //     radarLine.interpolate("cardinal-closed");
            // }
                        
            //Create a wrapper for the blobs	
            var blobWrapper = g.selectAll(".radarWrapper")
                .data(data)
                .enter().append("g")
                .attr("class", "radarWrapper");
                    
            //Append the backgrounds	
            blobWrapper
                .append("path")
                .attr("class", "radarArea")
                .attr("d", function(d,i) { return radarLine(d); })
                .style("fill", function(d,i) { return cfg.color(i); })
                .style("fill-opacity", cfg.opacityArea)
                .on('mouseover', function (d,i){
                    //Dim all blobs
                    d3.selectAll(".radarArea")
                        .transition().duration(200)
                        .style("fill-opacity", 0.1); 
                    //Bring back the hovered over blob
                    d3.select(this)
                        .transition().duration(200)
                        .style("fill-opacity", 0.7);
                })
                .on('mouseout', function(){
                    //Bring back all blobs
                    d3.selectAll(".radarArea")
                        .transition().duration(200)
                        .style("fill-opacity", cfg.opacityArea);
                });
                
            //Create the outlines	
            blobWrapper.append("path")
                .attr("class", "radarStroke")
                .attr("d", function(d,i) { return radarLine(d); })
                .style("stroke-width", cfg.strokeWidth + "px")
                .style("stroke", function(d,i) { return cfg.color(i); })
                .style("fill", "none")
                .style("filter" , "url(#glow)");		

            //Append the circles
            blobWrapper.selectAll(".radarCircle")
                .data(function(d,i) { return d; })
                .enter().append("circle")
                .attr("class", "radarCircle")
                .attr("r", cfg.dotRadius)
                .attr("cx", function(d,i){ return rScale(d.value) * Math.cos(angleSlice*i - Math.PI/2); })
                .attr("cy", function(d,i){ return rScale(d.value) * Math.sin(angleSlice*i - Math.PI/2); })
                .style("fill", function(d,i,j) { return cfg.color(j); })
                .style("fill-opacity", 0.8);

            //Wrapper for the invisible circles on top
            var blobCircleWrapper = g.selectAll(".radarCircleWrapper")
            .data(data)
            .enter().append("g")
            .attr("class", "radarCircleWrapper");
            
            //Append a set of invisible circles on top for the mouseover pop-up
            blobCircleWrapper.selectAll(".radarInvisibleCircle")
            .data(function(d,i) { return d; })
            .enter().append("circle")
            .attr("class", "radarInvisibleCircle")
            .attr("r", cfg.dotRadius*1.5)
            .attr("cx", function(d,i){ return rScale(d.value) * Math.cos(angleSlice*i - Math.PI/2); })
            .attr("cy", function(d,i){ return rScale(d.value) * Math.sin(angleSlice*i - Math.PI/2); })
            .style("fill", "none")
            .style("pointer-events", "all")
            .on("mouseover", function(d,i,j) {
                newX =  parseFloat(d3.select(this).attr('cx')) - 10;
                newY =  parseFloat(d3.select(this).attr('cy')) - 10;
                tooltip
                    .attr('x', newX)
                    .attr('y', newY)
                    .text(d.value)
                    .transition().duration(200)
                    .style('opacity', 1);
            })
            .on("mouseout", function(){
                tooltip.transition().duration(200)
                    .style("opacity", 0);
            });
            
            //Set up the small tooltip for when you hover over a circle
            var tooltip = g.append("text")
                .attr("class", "tooltip")
                .style("fill", "white")
                .style("opacity", 0);

            var data_legend = [
                {
                    "candidate" : "Biden",
                    "color" : bidenColor
                },
                {
                    "candidate" : "Trump",
                    "color" : trumpColor
                }
            ];

            var l = d3.select("#legand")
            .append("svg")
            .attr('width', 500)
            .attr('height',20)
            .attr('class', "svg");
            var legend = l.selectAll(".legend")
            .data(data_legend)
            .enter().append("g")
            .attr("class", "legend")
            .attr("transform", function(d, i) { return "translate(" + (i*70 + 70) + "," + 0  + ")"; });
        
            legend.append("rect")
            .attr("x", 10)
            // .attr("x", (width / 160) * 157)  
            .attr("y", 8)
            .attr("width", 15)
            .attr("height", 15) 
            .style("fill", function(d){
                return d.color
            });
        
    legend.append("text")
        .attr("x", 60)
        // .attr("x", (width / 40) * 39)
        .attr("y", 18)
        .style("text-anchor", "end") 
        .attr("font-size", '12px')
        .attr("fill", function(){
            return '#333'
            })
        .text(function(d) { 
            return d.candidate; })
    })

    function wrap(text, width) {
        text.each(function() {
          var text = d3.select(this),
              words = text.text().split(/\s+/).reverse(),
              word,
              line = [],
              lineNumber = 0,
              lineHeight = 1.4, // ems
              y = text.attr("y"),
              x = text.attr("x"),
              dy = parseFloat(text.attr("dy")),
              tspan = text.text(null).append("tspan").attr("x", x).attr("y", y).attr("dy", dy + "em");
              
          while (word = words.pop()) {
            line.push(word);
            tspan.text(line.join(" "));
            if (tspan.node().getComputedTextLength() > width) {
              line.pop();
              tspan.text(line.join(" "));
              line = [word];
              tspan = text.append("tspan").attr("x", x).attr("y", y).attr("dy", ++lineNumber * lineHeight + dy + "em").text(word);
            }
          }
        });
    }//wrap	
}

function section3slide1(){
    var widthRange = document.body.clientWidth/1852
    var heightRange = document.body.clientHeight/1009

    var trumpColor = '#f0b669';
    var bidenColor = '#dfa197';

    var color = d3.scaleOrdinal()
    .range([trumpColor,bidenColor,"#00A0B0"]);
    
    var width = 800 * widthRange
    var height = 450 * heightRange
    var svgTweet = d3.select("#tweetsMap")
        .append("svg")
        .attr("width", width)
        .attr("height", height);

    var svgVote = d3.select("#votesMap")
    .append("svg")
    .attr("width", width)
    .attr("height", height);

    var projection = d3.geoAlbersUsa()
        .scale([1000 * widthRange])
        .translate([width/2, height/2]);

    var path = d3.geoPath().projection(projection);

    var data = d3.map();
    
    d3.queue()
        .defer(d3.json, "usa.geojson")
        .defer(d3.csv, "d3_vote_tweet_data.csv", function(d) {data.set(d.state,
             [+d.trump_minus_biden, +d.sum_biden, +d.sum_trump,
                 +d.vote_trump_minus_biden, +d.vote_biden_total, +d.vote_trump_total]);})
        //.defer(d3.csv, "d3_vote_data.csv", function(d) {vote.set(d.state, [+d.trump_minus_biden, +d.sum_biden, +d.sum_trump]);})
        .await(ready);

    var margin = {top: 10, right: 10, bottom: 20, left: 60},
    barWidth = 200 * widthRange - margin.left - margin.right,
    barHeight = 250 * heightRange - margin.top - margin.bottom;

    // append the svg object to the body of the page
    var tweetBarSvg = d3.select("#tweetsBar")
    .append("svg")
    .attr("width", barWidth + margin.left + margin.right)
    .attr("height", barHeight + margin.top + margin.bottom)
    .append("g")
    .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    // Initialize the X axis
    var x = d3.scaleBand()
    .range([ 0, barWidth ])
    .padding(1);
    var xAxis = tweetBarSvg.append("g")
    .attr("transform", "translate(0," + barHeight + ")");

    // Initialize the Y axis
    var y = d3.scaleLinear()
    .range([ barHeight, 0]);
    var yAxis = tweetBarSvg.append("g")
    .attr("class", "myYaxis")

    // append the svg object to the body of the page
    var voteBarSvg = d3.select("#votesBar")
    .append("svg")
    .attr("width", barWidth + margin.left + margin.right)
    .attr("height", barHeight + margin.top + margin.bottom)
    .append("g")
    .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    // Initialize the X axis
    var x2 = d3.scaleBand()
    .range([ 0, barWidth ])
    .padding(1);
    var xAxis2 = voteBarSvg.append("g")
    .attr("transform", "translate(0," + barHeight + ")");

    // Initialize the Y axis
    var y2 = d3.scaleLinear()
    .range([ barHeight, 0]);
    var yAxis2 = voteBarSvg.append("g")
    .attr("class", "myYaxis")
    
    function ready(error, topo){

        let click = function(d){
            var tweetData = [
                {group : "Biden", value: d.tweet_biden},
                {group : "Trump", value: d.tweet_trump},
            ];
            var voteData = [
                {group : "Biden", value: d.vote_biden},
                {group : "Trump", value: d.vote_trump},
            ];

            var selected = d.properties.name;

            svgVote.selectAll("text")
            .text(function(d){
                 if(d.properties.name == selected){
                    return d.properties.name;
                 }
                })
            
            svgTweet.selectAll("text")
            .text(function(d){
                if(d.properties.name == selected){
                    return d.properties.name;
                }
                })

            // X axis
            x.domain(tweetData.map(function(d) { return d.group; }));
            xAxis.transition().duration(1000).call(d3.axisBottom(x));
            x2.domain(tweetData.map(function(d) { return d.group; }));
            xAxis2.transition().duration(1000).call(d3.axisBottom(x2));

            // Add Y axis
            y.domain([0, d3.max(tweetData, function(d) { return +d.value})]);
            yAxis.transition().duration(1000).call(d3.axisLeft(y));
            // Add Y axis
            y2.domain([0, d3.max(voteData, function(d) { return +d.value})]);
            yAxis2.transition().duration(1000).call(d3.axisLeft(y2));

            // variable u: map data to existing circle
            var j = tweetBarSvg.selectAll(".myLine")
            .data(tweetData)
            // update lines
            j
            .enter()
            .append("line")
            .attr("class", "myLine")
            .merge(j)
            .transition()
            .duration(1000)
            .attr("x1", function(d) { return x(d.group); })
            .attr("x2", function(d) { return x(d.group); })
            .attr("y1", y(0))
            .attr("y2", function(d) {return y(d.value); })
            .attr("stroke", "grey")
            .attr("stroke-width", "4");

            // variable u: map data to existing circle
            var u = tweetBarSvg.selectAll("circle")
            .data(tweetData)
            // update bars
            u
            .enter()
            .append("circle")
            .merge(u)
            .transition()
            .duration(1000)
            .attr("cx", function(d) { return x(d.group); })
            .attr("cy", function(d) { return y(d.value); })
            .attr("r", 5)
            .attr("fill", "#69b3a2");

            // variable u: map data to existing circle
            var j2 = voteBarSvg.selectAll(".myLine")
            .data(voteData)
            // update lines
            j2
            .enter()
            .append("line")
            .attr("class", "myLine")
            .merge(j2)
            .transition()
            .duration(1000)
            .attr("x1", function(d) { return x2(d.group); })
            .attr("x2", function(d) { return x2(d.group); })
            .attr("y1", y2(0))
            .attr("y2", function(d) {return y2(d.value); })
            .attr("stroke", "grey")
            .attr("stroke-width", "4");

            // variable u: map data to existing circle
            var u2 = voteBarSvg.selectAll("circle")
            .data(voteData)
            // update bars
            u2
            .enter()
            .append("circle")
            .merge(u2)
            .transition()
            .duration(1000)
            .attr("cx", function(d) { return x2(d.group); })
            .attr("cy", function(d) { return y2(d.value); })
            .attr("r", 5)
            .attr("fill", "#69b3a2");

            var msg1 = "";
            var msg2 = "";
            if(d.total > 0){
                msg1 = "Trump has " + d.total + " more tweets than Biden";
            }else{
                msg1 = "Biden has " + (-d.total) + " more tweets than Trump";
            }
            if(d.total_vote > 0){
                msg2 = "Trump has " + d.total_vote + " more votes than Biden";
            }else{
                msg2 = "Biden has " + (-d.total_vote) + " more votes than Trump";
            }

            var msg = d3.select("#msg");
            msg.text(msg1 + ", " + msg2);
        }

        let mouseOver = function(d) {
            var selected = "";
            d3.select(this)
              .transition()
              .duration(200)
              .style("opacity", 1)
              .style("color", function(d,i){
                selected = d.properties.name;
            })
            d3.selectAll(".Country")
              .transition()
              .duration(200)
              .style("opacity", function(d){
                  if(d.properties.name != selected){
                      return 0.5;
                  }
              })
          }
        
          let mouseLeave = function(d) {
            d3.selectAll(".Country")
              .transition()
              .duration(200)
              .style("opacity", 0.7)
          }

        svgTweet.append("g")
        .selectAll("path")
        .data(topo.features)
        .enter()
        .append("path")
        // draw each country
        .attr("d", path)
        .attr("class", function(d){ return "Country" } )
        // set the color of each country
        .attr("fill", function (d) {
            d.total = data.get(d.properties.name)[0] || 0;
            d.tweet_biden = data.get(d.properties.name)[1] || 0;
            d.tweet_trump = data.get(d.properties.name)[2] || 0;
            d.vote_biden = data.get(d.properties.name)[4] || 0;
            d.vote_trump = data.get(d.properties.name)[5] || 0;
             if(d.total > 0){
                 return trumpColor;
            }
             if(d.total < 0){
                 return bidenColor;
            }
            if(d.total = 0){
                return "white";
            }
            // d.total = data.get(d.id) || 0;
            // return colorScale(d.total);
            //console.log(d);
            //console.log(data);
        })
        .style("opacity", 0.7)
        .on("mouseover", mouseOver )
        .on("mouseleave", mouseLeave )
        .on("click",click);

        // Add the labels
        svgTweet.append("g")
        .selectAll("labels")
        .data(topo.features)
        .enter()
        .append("text")
            .attr("x", function(d){return path.centroid(d)[0]})
            .attr("y", function(d){return path.centroid(d)[1]})
            //.text(function(d){ return d.properties.name})
            .attr("text-anchor", "middle")
            .attr("alignment-baseline", "central")
            .style("font-size", 12)
            .style("fill", "white");

        svgVote.append("g")
        .selectAll("path")
        .data(topo.features)
        .enter()
        .append("path")
        // draw each country
        .attr("d", path)
        .attr("class", function(d){ return "Country" } )
        // set the color of each country
        .attr("fill", function (d) {
            d.total_vote = data.get(d.properties.name)[3] || 0;
            d.tweet_biden = data.get(d.properties.name)[1] || 0;
            d.tweet_trump = data.get(d.properties.name)[2] || 0;
            d.vote_biden = data.get(d.properties.name)[4] || 0;
            d.vote_trump = data.get(d.properties.name)[5] || 0;
            if(d.total_vote > 0){
                return trumpColor;
           }
            if(d.total_vote < 0){
                return bidenColor;
           }
           if(d.total_vote = 0){
               return "white";
           }
            // return colorScale(d.total);
            //console.log(d);
        })
        .style("opacity", 0.7)
        .on("mouseover", mouseOver )
        .on("mouseleave", mouseLeave )
        .on("click",click);

        // Add the labels
        svgVote.append("g")
        .selectAll("labels")
        .data(topo.features)
        .enter()
        .append("text")
            .attr("x", function(d){return path.centroid(d)[0]})
            .attr("y", function(d){return path.centroid(d)[1]})
            //.text(function(d){ return d.properties.name})
            .attr("text-anchor", "middle")
            .attr("alignment-baseline", "central")
            .style("font-size", 12)
            .style("fill", "white");

        var data_legend = [
            {
                "candidate" : "Biden",
                "color" : bidenColor
            },
            {
                "candidate" : "Trump",
                "color" : trumpColor
            }
        ];

        var l = d3.select("#tweetLegand")
        .append("svg")
        .attr('width', 550)
        .attr('height',20)
        .attr('class', "svg");

        var legend = l.selectAll(".legend")
        .data(data_legend)
        .enter().append("g")
        .attr("class", "legend")
        .attr("transform", function(d, i) { return "translate(" + (i*240 + 70) + "," + 0  + ")"; });
    
        legend.append("rect")
        .attr("x", 20)
        // .attr("x", (width / 160) * 157)  
        .attr("y", 8)
        .attr("width", 15)
        .attr("height", 15) 
        .style("fill", function(d){
            return d.color
        });

        legend.append("text")
        .attr("x", 230)
        // .attr("x", (width / 40) * 39)
        .attr("y", 18)
        .style("text-anchor", "end") 
        .attr("font-size", '12px')
        .attr("fill", function(){
            return '#333'
            })
        .text(function(d) { 
            return d.candidate + " has more tweets in this state"; })

        var l2 = d3.select("#voteLegand")
        .append("svg")
        .attr('width', 550)
        .attr('height',20)
        .attr('class', "svg");

        var legend2 = l2.selectAll(".legend")
        .data(data_legend)
        .enter().append("g")
        .attr("class", "legend")
        .attr("transform", function(d, i) { return "translate(" + (i*240 + 70) + "," + 0  + ")"; });
    
        legend2.append("rect")
        .attr("x", 20)
        // .attr("x", (width / 160) * 157)  
        .attr("y", 8)
        .attr("width", 15)
        .attr("height", 15) 
        .style("fill", function(d){
            return d.color
        });

        legend2.append("text")
        .attr("x", 230)
        // .attr("x", (width / 40) * 39)
        .attr("y", 18)
        .style("text-anchor", "end") 
        .attr("font-size", '12px')
        .attr("fill", function(){
            return '#333'
            })
        .text(function(d) { 
            return d.candidate + " has more votes in this state"; })
        }

}

function section3slide2(){
    var widthRange = document.body.clientWidth/1852
    var heightRange = document.body.clientHeight/1009

    var trumpColor = '#f0b669';
    var bidenColor = '#dfa197';
    // set the dimensions and margins of the graph
    var margin = {top: 10, right: 30, bottom: 30, left: 60},
    width = 500 * widthRange - margin.left - margin.right,
    height = 400 * heightRange - margin.top - margin.bottom;

    var yesCount = 0;
    var noCount = 0;

    yesCount = parseInt(localStorage.getItem("yes")) || 0;
    noCount = parseInt(localStorage.getItem("no")) || 0;
    //localStorage.setItem("ifClick","true");

    var yesBtn = document.getElementById("yes");
    var noBtn = document.getElementById("no");


    yesBtn.onclick = function(d){
        yesCount += 1;
        localStorage.setItem("yes",yesCount + "");
        d3.select("#btnMsg").text("Your choice: yes");
        showFigures();
    }
    noBtn.onclick = function(d){
        noCount += 1;
        localStorage.setItem("no",noCount + "");
        d3.select("#btnMsg").text("Your choice: no");
        showFigures();
    }

    function showFigures(){
        yesBtn.style.display = "none";
        noBtn.style.display = "none";

        d3.select("#scatterMsg1").text("Actually, the two scatter plots tell us that states with more tweets are more likely to also have more votes.In general, the correlation is similar, and when the amount of tweets increases, Biden seems to have more votes than Trump.");
        d3.select("#guessResult").text("The answer is yes. Do you have a right guess?")

        // set the dimensions and margins of the graph
        var pieWidth = 350 * widthRange;
        var pieHeight = 350 * widthRange;
        var pieMargin = 40;

        // The radius of the pieplot is half the width or half the height (smallest one). I subtract a bit of margin.
        var radius = Math.min(pieWidth, pieHeight) / 2 - pieMargin

        // shape helper to build arcs:
        var arcGenerator = d3.arc()
        .innerRadius(0)
        .outerRadius(radius)

        // append the svg object to the div called 'my_dataviz'
        var pieSvg = d3.select("#pie")
        .append("svg")
        .attr("width", pieWidth)
        .attr("height", pieHeight)
        .append("g")
        .attr("transform", "translate(" + pieWidth / 2 + "," + pieHeight / 2 + ")");

        var pieData = {yes: yesCount, no: noCount}
        console.log(pieData);
        // set the color scale
        var pieColor = d3.scaleOrdinal()
        .domain(pieData)
        .range(["#98abc5", "#8a89a6"])

        // Compute the position of each group on the pie:
        var pie = d3.pie()
        .value(function(d) {return d.value; })
        var data_ready = pie(d3.entries(pieData))

        // Build the pie chart: Basically, each part of the pie is a path that we build using the arc function.
        pieSvg
        .selectAll('mySlices')
        .data(data_ready)
        .enter()
        .append('path')
        .attr('d', arcGenerator)
        .attr('fill', function(d){ return(pieColor(d.data.key)) })
        .attr("stroke", "black")
        .style("stroke-width", "2px")
        .style("opacity", 0.7)

        // Now add the annotation. Use the centroid method to get the best coordinates
        pieSvg
        .selectAll('mySlices')
        .data(data_ready)
        .enter()
        .append('text')
        .text(function(d){ return  Math.round((d.value * 100/(yesCount + noCount)),2) + "% " + d.data.key})
        .attr("transform", function(d) { return "translate(" + arcGenerator.centroid(d) + ")";  })
        .style("text-anchor", "middle")
        .attr("fill", "white")
        .style("font-size", 17)
 

        // append the svg object to the body of the page
        var svgTrump = d3.select("#trumpScatter")
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

        d3.csv("d3_scatter_trump.csv", function(data) {

            // Add X axis
            var x = d3.scaleLinear()
            .domain([0, 32000])
            .range([ 0, width ]);
            svgTrump.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x));
        
            // Add Y axis
            var y = d3.scaleLinear()
            .domain([0, 6010000])
            .range([ height, 0]);
            svgTrump.append("g")
            .call(d3.axisLeft(y));
        
            // Add dots
            svgTrump.append('g')
            .selectAll("dot")
            .data(data)
            .enter()
            .append("circle")
            .transition()
            .delay(function(d,i){return(i*3)})
            .duration(2000)
                .attr("cx", function (d) { return x(d.sum_trump); } )
                .attr("cy", function (d) { return y(d.trump_total); } )
                .attr("r", 3)
                .style("fill", trumpColor)
                .style("stroke-width", "0");
        
        })

        // append the svg object to the body of the page
        var svgBiden = d3.select("#bidenScatter")
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

        d3.csv("d3_scatter_biden.csv", function(data) {

            // Add X axis
            var x = d3.scaleLinear()
            .domain([0, 26000])
            .range([ 0, width ]);
            svgBiden.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x).ticks(7));
        
            // Add Y axis
            var y = d3.scaleLinear()
            .domain([0, 11110000])
            .range([ height, 0]);
            svgBiden.append("g")
            .call(d3.axisLeft(y));
        
            // Add dots
            svgBiden.append('g')
            .selectAll("dot")
            .data(data)
            .enter()
            .append("circle")
            .transition()
            .delay(function(d,i){return(i*3)})
            .duration(2000)
                .attr("cx", function (d) { return x(d.sum_biden); } )
                .attr("cy", function (d) { return y(d.biden_total); } )
                .attr("r", 3)
                .style("fill", bidenColor)
                .style("stroke-width", "0");
        
        })

    }
}