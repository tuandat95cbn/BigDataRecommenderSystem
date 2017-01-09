function sort = sortSim(a)
 [m n] = size(a);
 for i = 1:n
     for j = i:n
        if(a(1,j) > a(1,i))
           temp = a(1,j);
           a(1,j) = a(1,i);
           a(1,i) = temp;
           temp = a(2,j);
           a(2,j) = a(2,i);
           a(2,i) = temp;
        end
     end
 end
 sort = a;
end