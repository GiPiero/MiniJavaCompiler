        .global start
start:
main:
        save    %sp, -104, %sp
        mov     0, %o0
        call new_object
        nop
        mov     %o0, %l0
        mov     %l0, %o0
        mov     10, %o1
        call Fac$ComputeFac
        nop
        mov     %o0, %l0
        mov     %l0, %o0
        call print_int
        nop
        mov     %o0, %l0
        ba      end002
        nop
end002:
        clr     %o0
        mov     1, %g1
        ta      0x90
        ret
        restore
Fac$ComputeFac:
        save    %sp, -104, %sp
        cmp     %i1, 1
        bl      t004
        nop
f005:
        mov     %i1, %l0
        mov     %i0, %o0
        sub     %i1, 1, %o1
        call Fac$ComputeFac
        nop
        mov     %o0, %l1
        smul    %l0, %l1, %l1
        mov     %l1, %l0
        ba      join006
        nop
join006:
        mov     %l0, %i0
        ba      end003
        nop
t004:
        mov     1, %l0
        ba      join006
        nop
end003:
        ret
        restore
