
%A=[3 4 0 5 6 7;0 0 0 4 5 1;0 1 2 3 0 9;0 9 2 1 3 3]
%[S V D] = svds(A,40);
%V=V*D';
%A=[3 4 0 5 6 7;0 0 0 4 5 1;0 1 2 3 0 9;0 9 2 1 3 3]
[rr cc vv]=find(A,100000);
x=randperm(length(rr),2000);
rr=rr(x);
cc=cc(x);
vv=vv(x);
A(rr,cc)=2.5;
S=rand(138493,30);
V=rand(30,27278);
%vt=550;
%T=A(vt:671,:);
%A(vt:671,:)=0;
N=size(A);
ngi=0.000099;
ngi1=0.000099;
k=10;
c=[];
for epoch=1:100000 
    %B=(A-(S*V)).^2;
    %disp(sum(B(:)));
    disp(epoch);
   
    for x=1:N(1)
        
        p=zeros(1,length(S(x,:)));
        q=zeros(1,length(p));
        v=randperm(N(2),k);
        for j=v
            if (sum(cc==j)==0) || (sum(rr==x)==0)
            e=2*(A(x,j)-S(x,:)*V(:,j));
            p=ngi*(e*V(:,j)'-0.02*S(x,:));
            q=ngi1*(e*S(x,:)-0.02*V(:,j)');
            S(x,:)=S(x,:)+p;
            V(:,j)=V(:,j)+q';
            end
        end
        
    end
    rex=0;
    for i=1:(length(rr)/2)
        rex=rex+(S(rr(i),:)*V(:,cc(i))-vv(i))^2;
    end
    disp(sqrt(rex/(length(rr)/2)));
    if(length(c)<2) 
        c=[c sqrt(rex/(length(rr)/2))];
    else  
        c=[c(2:length(c)) sqrt(rex/(length(rr)/2))];
        if (c(length(c))-c(1)) >0.01
            break;
           %ngi=ngi-rand(1,1)*0.5*ngi;
          % ngi1=ngi1-rand(1,1)*0.5*ngi1;
          % disp('change learning rate');
          % disp(ngi);
           %disp(ngi1);
           %c=[];
        end
        
    end
    %if(sqrt(rex/length(rr))<1.6) 
     %   break;
    %end
end
%[S V D] = svds(A,100);
%V=V*D';
rex=0;
for i=length(rr)/2+1:length(rr)
    rex=rex+(S(rr(i),:)*V(:,cc(i))-vv(i))^2;
end
res=sqrt(rex/(length(rr)-length(rr)/2));

