function matrix = Content_Matrix(a,rating)
[m n] = size(a);
matrix = [];
for i = 1:m
   b = zeros(1,n);
   for  j = 1:n
       if(a(i,j) == 0)
           continue;
       elseif(a(i,j) >= rating)
           b(j) = 1;
       else
           b(j) = -1;
       end
   end
   matrix = [matrix;b];
end
end