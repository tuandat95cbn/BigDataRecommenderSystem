X = importdata('RecommandSystem\Features.txt');
A = load('RecommandSystem\Matrix_Content.txt');
% nen matrix
A = sparse(A);
% load matrix
User_Movie = load('RecommandSystem\matrix.mat');
B = User_Movie.A;
user = B(1,:);
for i = 1:100
    user(i) = 0;
end
weight = weightFeature(B(1,:),A);
B_test = Content_Matrix(user,3.5);
user_feature = B_test*A;
user_feature = Content_Matrix(user_feature,0).*weight;
% sim jaccard
h = 10000;
% index movie
index = [];
for i = 1:h
    index = [index i];
end
D = pdist2(user_feature,A(1:10000,:),'cosine');
D = [D;index];
D = sortSim(D);
%D = pdist2(user_feature,A,'jaccard');
%D = [D;index];
%D = sortSim(D);







