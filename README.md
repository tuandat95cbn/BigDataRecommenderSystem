# BigDataRecommenderSystem
Movie Len
## Tóm tắt
Bài tập lớn bigdata:
Bọn em cài đặt và thử nghiệm 3 thuật toán cho bài toán gợi ý trên dữ liệu phim của movie len:<br />
Bộ dữ liệu: ml-20m ( size: 190MB ).<br />
Download: http://grouplens.org/datasets/movielens/.<br />
Bộ dữ liệu mô tả đánh giá 1 - 5 sao phim từ MovieLens.<br />
Bộ dữ liệu chứa:<br />
- 20000263 rating và 465564 tag của 27278 bộ phim.
- Dữ liệu đươc tạo bởi 138493 users.
- Các users được thu thập ngẫu nhiên. Tất cả users đã đánh giá ít nhất
20 bộ phim.
- Các file dữ liệu chứa trong 6 tập, genome-scores.csv,
genome-tags.csv, links.csv, movies.csv, ratings.csv và tags.csv.
- Các file đã sử dụng: movies.csv và ratings.csv.

## Thuật toán sử dụng
- Collaborative Filtering
- Latent Factor Model
- Content-based

## Thực hiện 
###Collaborative Filtering 
- Thực hiện trên ngôn ngữ Java
- Sử dụng nén thưa để fit toàn bộ ma trận dữ liệu vào bộ nhớ
- Tách lấy 1000 rating để đoán.
- Chọn k=5,10
- Kết hợp với baseline(trung bình trọng số) để đoán cho tập 1000 rating đã tách.

###Latent Factor Model
- Tách lấy 2000 rating một nửa làm tập validation một nửa làm test  phần còn lại làm tập train.
- Chọn factor k=30,50,100 (30).
- Chọn nguy=0.0001.
- Chọn lamda=0.02.
- Sử dụng Stochastic Gradient Descent.

###Content-based

##Kết quả
- Collaborative Filtering(k=5) : 1.13
- Latent Factor Model($\lambda_1=\lambda_2=0.002, SGD)$ : 3.5(cũ) ?? (update 1.2)

## Các file trong bài tập
- Thư mục src chứa code java xử lý dữ liệu content-base và Collaborative Filtering 
- Thư mục matlab chứa code matlab cho Latent Factor Model

- File CF2.java thực hiện thuật toán Collaborative Filtering 
- File Content-base.java thực hiện thuật toán content-base
- File main.m LFM.m  thực hiện thuật toán Latent Factor Model

## Các bước thực hiện 
1. Chạy MatrixItemAndFeatures.java để trích dữ liệu và maping
2. Chạy file MatrixRatingMovie.java tạo ra ma trận rating-maping(14GB)
3. Chạy các file thuật toán(riêng với file main.m LFM phải để trong thư mục Data)





