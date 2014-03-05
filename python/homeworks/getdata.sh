baseURL='http://www.cse.ohio-state.edu/~srini/674/public/reuters/'
dataDirectory="data"

cd $data
for i in {00..21}
do
 url=$baseURL
 url+="reut2-0$i.sgm"
 wget $url
done
