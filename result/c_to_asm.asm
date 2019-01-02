datasg segment
tem db 6,7 dup  (0)
sum dw 0
n dw 0
T1 dw 0
T2 dw 0
T3 dw 0
scanf_n1 db 'input n:$'
printf_sum9 db 'sum:$'
datasg ends
codesg segment
assume cs:codesg,ds:datasg
start:
MOV AX,datasg
MOV DS,AX
L1:


;SCANF
lea dx,scanf_n1
mov ah,9
int 21h
;输入中断
mov al,0h;
mov tem[1],al;
lea dx,tem;
 mov ah,0ah
int 21h
;处理输入的数据，并赋值给变量
mov cl,0000h;
mov al,tem[1];
sub al,1;
mov cl,al;
mov ax,0000h;
mov bx,0000h;
mov al,tem[2];
sub al,30h;
mov n,ax;
mov ax,cx
sub ax,1
jc inputEnd1
;
MOV SI,0003H;
ln1:mov bx,10;
mov ax,n;
mul bx;
mov n,ax;
mov ax,0000h;
mov al,tem[si]
sub al,30h;
add ax,n;
mov n,ax
INC SI
loop ln1
inputEnd1: nop


;换行
mov dl,0dh
mov ah,2
int 21h
mov dl,0ah
mov ah,2
int 21h


L2: mov AX, n
sub AX, 1
L3: jnc L7
L4: mov AX, n
add AX, 1
mov T2, AX
L5: mov AX, T2
mov sum, AX
L6: jmp TheEnd
L7: mov AX, n
sub AX, 1
mov T3, AX
L8: mov AX, T3
mov sum, AX
TheEnd:nop


;PRINTF
L9:
lea dx,printf_sum9
mov ah,9
int 21h
mov ax,sum
xor cx,cx
mov bx,10
PT09:xor dx,dx
div bx
or dx,0e30h;0e:显示字符
push dx
inc cx
cmp ax,0;ZF=1则AX=0,ZF=0则AX！=0
jnz PT09;相等时跳转
PT19:pop ax
int 10h;显示一个字符
loop PT19
mov ah,0 
;int 16h ;键盘中断
;换行
mov dl,0dh
mov ah,2
int 21h
mov dl,0ah
mov ah,2
int 21h


mov ax,4c00h; int 21h的4ch号中断，安全退出程序。
int 21h;调用系统中断
codesg ends
end start
